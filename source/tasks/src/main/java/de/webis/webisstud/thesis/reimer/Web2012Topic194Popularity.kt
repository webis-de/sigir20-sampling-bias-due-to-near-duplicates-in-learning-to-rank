package de.webis.webisstud.thesis.reimer

import de.webis.webisstud.thesis.reimer.corpus.url.urls
import de.webis.webisstud.thesis.reimer.groups.fingerprintGroups
import de.webis.webisstud.thesis.reimer.math.stats
import de.webis.webisstud.thesis.reimer.model.Corpus.Companion.CLUE_WEB_12_ALEXA_DATE
import de.webis.webisstud.thesis.reimer.model.Document
import de.webis.webisstud.thesis.reimer.model.Topic
import de.webis.webisstud.thesis.reimer.model.TrecTask
import dev.reimer.alexa.AlexaTopRanks
import dev.reimer.alexa.get
import dev.reimer.domain.ktx.Domain
import java.net.URL

fun main() {
	val task = TrecTask.Web2012
	val topic = task.getTopic("194")
	val urls = topic.urls
	val alexaRanks = AlexaTopRanks.getBlocking(CLUE_WEB_12_ALEXA_DATE)
	val groups = task.corpus.fingerprintGroups

	printStats(topic, urls, alexaRanks)
	printStats(topic, urls, alexaRanks, "relevant") { it.isRelevant }
	printStats(topic, urls, alexaRanks, "irrelevant") { !it.isRelevant }
	printStats(topic, urls, alexaRanks, "with duplicates") { groups.anyGroupContains(it.id) }
	printStats(topic, urls, alexaRanks, "without duplicates") { !groups.anyGroupContains(it.id) }
	printStats(topic, urls, alexaRanks, "relevant, with duplicates") { it.isRelevant && groups.anyGroupContains(it.id) }
	printStats(topic, urls, alexaRanks, "relevant, without duplicates") { it.isRelevant && !groups.anyGroupContains(it.id) }

	val alexaRankRelevantWithDuplicates = topic
			.averageAlexaRank(urls, alexaRanks) { it.isRelevant && groups.anyGroupContains(it.id) }
	val alexaRankRelevantWithoutDuplicates = topic
			.averageAlexaRank(urls, alexaRanks) { it.isRelevant && !groups.anyGroupContains(it.id) }
	val hypothesis = alexaRankRelevantWithDuplicates < alexaRankRelevantWithoutDuplicates

	println("Hypothesis ($alexaRankRelevantWithDuplicates < $alexaRankRelevantWithoutDuplicates) is $hypothesis for topic 194.")
}

fun <T : Document> printStats(
		topic: Topic<T>,
		urlCache: Map<String, URL>,
		alexaCache: Map<Domain, Long>,
		label: String? = null,
		predicate: (document: T) -> Boolean = { true }
) {
	println("Stats for topic ${topic.title}${if (label != null) " ($label)" else ""}:")
	val documents = topic.documents.filter { predicate(it) }
	println("Size: ${documents.size}")
	val urls = documents.map { urlCache[it.id] }
	println("Hostnames: ${urls.joinToString(limit = 5)} (total: ${urls.size})")
	val alexaRanks = urls.map { if (it != null) alexaCache[it] else null }
	println("Alexa ranks: ${alexaRanks.joinToString(limit = 5)} (total: ${alexaRanks.size})")
	println("Alexa rank stats: ${alexaRanks.filterNotNull().stats()}")
	println()
}