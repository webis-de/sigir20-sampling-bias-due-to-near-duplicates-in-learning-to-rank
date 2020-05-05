package de.webis.webisstud.thesis.reimer

import de.webis.webisstud.thesis.reimer.corpus.url.urls
import de.webis.webisstud.thesis.reimer.groups.canonical.CanonicalFingerprintGroup
import de.webis.webisstud.thesis.reimer.groups.canonical.canonicalFingerprintGroups
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.JudgedDocument
import de.webis.webisstud.thesis.reimer.model.trecDocuments
import de.webis.webisstud.thesis.reimer.model.trecTopics
import dev.reimer.domain.ktx.domain

data class RelevanceDegrees(
        val all: Double,
        val deduplicated: Double,
        val wikipedia: Double,
        val wikipediaDeduplicated: Double
)

fun Iterable<JudgedDocument>.computeRelevanceDegrees(corpus: Corpus): RelevanceDegrees {
	val all = toList().also { println(it.size) }

	val urls = corpus.urls
	val groups = corpus.canonicalFingerprintGroups.groups

	val wikipedia = filter { urls[it.id]?.domain?.root == "wikipedia.org" }.also { println(it.size) }
	val deduplicated = all.filterCanonicalIn(groups).also { println(it.size) }
	val wikipediaDeduplicated = wikipedia.filterCanonicalIn(groups).also { println(it.size) }

	return RelevanceDegrees(
			all.filter { it.isRelevant }.size.toDouble() / all.size,
			deduplicated.filter { it.isRelevant }.size.toDouble() / deduplicated.size,
			wikipedia.filter { it.isRelevant }.size.toDouble() / wikipedia.size,
			wikipediaDeduplicated.filter { it.isRelevant }.size.toDouble() / wikipediaDeduplicated.size
	)
}

private fun Collection<JudgedDocument>.filterCanonicalIn(canonicalGroups: Set<CanonicalFingerprintGroup>): List<JudgedDocument> {
    val nonCanonicalIds: Set<String> = canonicalGroups.flatMapTo(mutableSetOf()) { group -> group.ids - group.globalCanonical }
    return filterNot { document ->
        document.id in nonCanonicalIds
    }
}

fun main(args: Array<String>) {
    val corpus = Corpus.valueOfCaseInsensitive(args[0])
    val topicIds = args.drop(1)
    val documents = when {
        topicIds.isNotEmpty() -> corpus.trecTopics.filter { it.id in topicIds }.flatMap { it.documents.asSequence() }
        else -> corpus.trecDocuments
    }.toList()
    val stats = documents.computeRelevanceDegrees(corpus)
    println(stats)
}