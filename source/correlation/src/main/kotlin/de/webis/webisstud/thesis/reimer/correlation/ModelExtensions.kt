package de.webis.webisstud.thesis.reimer.correlation

import de.webis.webisstud.thesis.reimer.corpus.url.urls
import de.webis.webisstud.thesis.reimer.data.TopicFiles
import de.webis.webisstud.thesis.reimer.data.files
import de.webis.webisstud.thesis.reimer.groups.fingerprintGroups
import de.webis.webisstud.thesis.reimer.math.kendallTau
import de.webis.webisstud.thesis.reimer.model.Corpus.Companion.CLUE_WEB_12_ALEXA_DATE
import de.webis.webisstud.thesis.reimer.model.JudgedTopic
import de.webis.webisstud.thesis.reimer.model.JudgedTopicTask
import dev.reimer.alexa.AlexaTopRanks
import dev.reimer.alexa.get
import kotlinx.serialization.internal.StringSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.File

private val serializer = CorrelationMatrix.serializer(StringSerializer)
private val json = Json(JsonConfiguration.Stable.copy(prettyPrint = true))

fun JudgedTopic<*>.rankCorrelations(): CorrelationMatrix<String>? {
    println("Correlations for topic $id from ${task.title ?: task.id}.")
    if (isEmpty()) return null

    val docsRunFile = files.runs.readRunFileIds()
    val urls = urls
    val alexaRanks = AlexaTopRanks.getBlocking(CLUE_WEB_12_ALEXA_DATE)
    val docsSortedByAlexa = docsRunFile
            .sortedBy { id ->
                urls[id]?.let { alexaRanks[it] } ?: 1000001L
            }

    fun CorrelationMatrix<String>.addRerankedRunsCorrelation(files: TopicFiles<*>) {
        val docsRunFileReranked = files.rerankedRuns.readRunFileIds()

        val tags = setOf(RankNames.reranked) + files.tags
        val rankName = tags.joinToString(" ", transform = String::toLowerCase)

        val correlationAlexa = docsSortedByAlexa kendallTau docsRunFileReranked
        set(RankNames.alexa, rankName, correlationAlexa)

        val correlationRunFile = docsRunFile kendallTau docsRunFileReranked
        set(RankNames.bm25, rankName, correlationRunFile)
    }

    val matrix = CorrelationMatrix<String>()
    matrix.addRerankedRunsCorrelation(files.tag("Duplicate removed"))

    val groups = task.corpus.fingerprintGroups
    val topicGroups = groups.filter(this)
    for (group in topicGroups) {
        val groupFiles = files.tag("Group ${group.hash}")
        for (window in group.idSubLists) {
            val windowFiles = groupFiles.tag("Take ${window.size}")
            matrix.addRerankedRunsCorrelation(windowFiles)
        }
    }

    files.rankCorrelations.writer().use { writer ->
        val encoded = json.stringify(serializer, matrix)
        writer.write(encoded)
    }

    println(matrix)

    return matrix
}

fun JudgedTopicTask<*, *>.rankCorrelations() = topics.map { it.rankCorrelations() }

fun Iterable<JudgedTopic<*>>.rankCorrelations() = map { it.rankCorrelations() }

fun Iterable<JudgedTopic<*>>.rankCorrelationHypothesis() {
    var correlatesMoreWithDuplicates = 0
    var correlatesMoreWithoutDuplicates = 0
    var total = 0
    for (topic in this) {
        val correlations = topic.rankCorrelations() ?: continue

        val reranked = correlations[RankNames.alexa, RankNames.reranked]
        val rerankedDuplicatesRemoved = correlations[RankNames.alexa, "${RankNames.reranked} duplicate removed"]
        println("τ('sorted by alexa rank', 'run file reranked'):                    $reranked")
        println("τ('sorted by alexa rank', 'run file reranked without duplicates'): $rerankedDuplicatesRemoved")
        when {
            reranked > rerankedDuplicatesRemoved -> {
                correlatesMoreWithDuplicates++
            }
            reranked < rerankedDuplicatesRemoved -> {
                correlatesMoreWithoutDuplicates++
            }
        }
        total++
        println("$correlatesMoreWithDuplicates of $total correlate more with duplicates.")
        println("$correlatesMoreWithoutDuplicates of $total correlate more without duplicates.")
    }
}

private fun File.readRunFileIds(): List<String> {
    return useLines { lines ->
        lines.map { line ->
            line.split(" ")[2]
        }.toList()
    }
}
