package de.webis.webisstud.thesis.reimer.domain.stats

import de.webis.webisstud.thesis.reimer.corpus.url.urls
import de.webis.webisstud.thesis.reimer.data.dataDir
import de.webis.webisstud.thesis.reimer.groups.fingerprintGroups
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.JudgedDocument
import de.webis.webisstud.thesis.reimer.model.trecDocuments
import dev.reimer.domain.ktx.domainOrNull
import dev.reimer.kotlin.jvm.ktx.*
import dev.reimer.serialization.jsonl.JsonL
import kotlinx.serialization.Serializable
import java.io.File
import kotlin.system.exitProcess

fun main(vararg args: String) {
	val corpus = Corpus.valueOfCaseInsensitive(args[0])
	val onlyDuplicates = args.getOrNull(1) in arrayOf("--redundant", "-r")
	val stats = corpus.domainStats(onlyDuplicates)
	val dataDir = corpus.dataDir
	val fileNameSuffix = if (onlyDuplicates) "-redundant" else ""
	stats.saveJsonL(dataDir.resolve("domain-stats$fileNameSuffix.jsonl"))
	stats.saveLatexTable(dataDir.resolve("domain-stats$fileNameSuffix.tex"), 100)
	stats.saveDomainLatexTable(dataDir.resolve("wikipedia-stats$fileNameSuffix.tex"), "wikipedia.org")
	stats.printDomainPortion("wikipedia.org")
	stats.printGlobalRelevanceDegree()
	exitProcess(0)
}

private val JSON_L = JsonL()

fun Corpus.domainStats(onlyDuplicates: Boolean): List<DomainStats> {
	return when {
		onlyDuplicates -> {
			val duplicateIds = fingerprintGroups
					.groups
					.flatMap { it.ids }
					.distinct()
            domainStats { it.id in duplicateIds }
        }
        else -> domainStats()
    }
}

fun Corpus.domainStats(predicate: (JudgedDocument) -> Boolean = { true }): List<DomainStats> {
	val urls = urls

	val documents = trecDocuments
			.filter(predicate)
			.map { it to it.id }
			.toMap()
			.inverse()
	val ids = documents.keys

	val domainId = ids.map { it to urls[it]?.domainOrNull?.root }
			.toMap()
			.filterValueNotNull()
			.inverse()
	val domainRelevance = domainId
			.mapValues { (_, ids) ->
				val judgements = ids
						.flatMapToMap { id ->
							documents.getValue(id).mapToMap { it.topic to it.judgement }
						}
				print(".")
				judgements
			}
	println()

	val domains = domainId.keys

	return domains.map { domain ->
		DomainStats(
				domain,
				domainRelevance.getValue(domain)
						.map { (topic, judgements) ->
							DomainStats.Relevance(topic.task.id, topic.id.toInt(), judgements)
						}
		)
    }
}

fun Collection<DomainStats>.saveLatexTable(file: File, n: Int = size) {
    file.prepareNewFile()
    asSequence()
            .sortedByDescending { it.documents }
            .take(n)
            .map { stats ->
	            """    ${stats.domain} & TODO & ${stats.relevantDocuments} & ${stats.irrelevantDocuments} & ${stats.documents} & ${"%.2f".format(stats.relevanceDegree * 100)}\,\% \\"""
            }
            .prepend(
                    """\begin{tabular}{@{}lrrrr@{}}""",
                    """    \toprule""",
                    """    \textbf{Domain} & \textbf{Tag} & \textbf{Rel.} & \textbf{Irrel.} & \textbf{Count} & \textbf{\%~Rel.} \\""",
                    """    \midrule"""
            )
            .append(
                    """    \bottomrule""",
                    """\end{tabular}"""
            )
            .writeLinesTo(file)
}

fun Collection<DomainStats>.printGlobalRelevanceDegree() {
    var globalDocuments = 0
    var relevantDocuments = 0
    for (stats in this) {
        globalDocuments += stats.documents
        relevantDocuments += stats.relevantDocuments
    }
    val portionPercent = 100.0 * relevantDocuments / globalDocuments
    println("Relevant documents make up for $relevantDocuments of $globalDocuments documents (${"%.2f".format(portionPercent)}%).")
}

fun Collection<DomainStats>.printDomainPortion(domain: String) {
    var globalDocuments = 0
    var domainDocuments = 0
    for (stats in this) {
        globalDocuments += stats.documents
        if (stats.domain == domain) {
            domainDocuments += stats.documents
        }
    }
    val portionPercent = 100.0 * domainDocuments / globalDocuments
    println("Domain '$domain' makes up for $domainDocuments of $globalDocuments documents (${"%.2f".format(portionPercent)}%).")
}

fun Collection<DomainStats>.saveDomainLatexTable(file: File, domain: String) {
    file.prepareNewFile()
    asSequence()
            .filter { it.domain == domain }
            .flatMap { it.relevance.asSequence() }
            .groupBy { it.topicId }
            .asSequence()
            .map { (topic, relevance) ->
	            topic to DomainStats(
			            domain = domain,
			            relevance = relevance
	            )
            }
            .sortedByDescending { (_, stats) -> stats.documents }
            .map { (topic, stats) ->
                """    $topic & ${"%.2f".format(stats.relevanceDegree)}\% & ${stats.documents} \\"""
            }
            .prepend(
                    """\begin{tabular}{@{}lrr@{}}""",
                    """    \toprule""",
                    """    \textbf{Topic} & \textbf{\%~Rel.\ Doc.} & \textbf{\#~Doc.} \\""",
                    """    \midrule"""
            )
            .append(
                    """    \bottomrule""",
                    """\end{tabular}"""
            )
            .writeLinesTo(file)
}

fun Iterable<DomainStats>.saveJsonL(file: File) {
    file.prepareNewFile()
    val stats = sortedByDescending { it.documents }
            .asSequence()
    JSON_L.stringify(DomainStats.serializer(), stats)
            .writeLinesTo(file)
}

@Serializable
data class DomainStats(
        val domain: String,
        val relevance: List<Relevance>
) {

    private val judgements get() = relevance.flatMap { it.judgements }

    val documents get() = judgements.count()

    val relevantDocuments get() = judgements.count { it > 0 }

    val irrelevantDocuments get() = judgements.count { it <= 0 }

    val relevanceDegree: Double
        get() {
            val judgements = judgements
            return judgements.count { it > 0 } / judgements.size.toDouble()
        }

    @Serializable
    data class Relevance(
            val taskId: String,
            val topicId: Int,
            val judgements: List<Int>
    )
}