package de.webis.webisstud.thesis.reimer

import de.webis.webisstud.thesis.reimer.corpus.url.urls
import de.webis.webisstud.thesis.reimer.groups.fingerprintGroups
import de.webis.webisstud.thesis.reimer.model.Corpus.Companion.CLUE_WEB_12_ALEXA_DATE
import de.webis.webisstud.thesis.reimer.model.TrecTask
import dev.reimer.alexa.AlexaTopRanks

fun main() {
    val task = TrecTask.Web2012
    val groups = task.corpus.fingerprintGroups

    var hypothesisTrueCount = 0
    var hypothesisFalseCount = 0
    for (topic in task.topics) {
        val urls = topic.urls
        val alexaRanks = AlexaTopRanks.getBlocking(CLUE_WEB_12_ALEXA_DATE)

        val idsRelevantCount = topic.documents.filter { it.isRelevant }.size
        val idsRelevantWithDuplicatesCount = topic.documents.filter { it.isRelevant && groups.anyGroupContains(it.id) }.size
        val idsRelevantWithoutDuplicatesCount = topic.documents.filter { it.isRelevant && !groups.anyGroupContains(it.id) }.size

        println("Topic $topic has $idsRelevantCount relevant documents.")
        println("Relevant documents with duplicates: $idsRelevantWithDuplicatesCount")
        println("Relevant documents without duplicates: $idsRelevantWithoutDuplicatesCount")

        when {
            idsRelevantWithDuplicatesCount == 0 -> {
                println("Skip hypothesis test, because there is no relevant document with duplicates in topic $topic.")
            }
            idsRelevantWithoutDuplicatesCount == 0 -> {
                println("Skip hypothesis test, because there is no relevant document without duplicates in topic $topic.")
            }
            else -> {

                val alexaRankRelevantWithDuplicates = topic
		                .averageAlexaRank(urls, alexaRanks) { it.isRelevant && groups.anyGroupContains(it.id) }
                val alexaRankRelevantWithoutDuplicates = topic
		                .averageAlexaRank(urls, alexaRanks) { it.isRelevant && !groups.anyGroupContains(it.id) }

                val hypothesis = alexaRankRelevantWithDuplicates < alexaRankRelevantWithoutDuplicates
                println("Hypothesis ($alexaRankRelevantWithDuplicates < $alexaRankRelevantWithoutDuplicates) is $hypothesis for topic $topic.")
                println("For topic $topic, relevant duplicates are ${
                when {
                    alexaRankRelevantWithDuplicates < alexaRankRelevantWithoutDuplicates -> "more"
                    alexaRankRelevantWithDuplicates > alexaRankRelevantWithoutDuplicates -> "less"
                    else -> "as"
                }
                } popular ${
                when {
                    alexaRankRelevantWithDuplicates != alexaRankRelevantWithoutDuplicates -> "than"
                    else -> "as"
                }
                } relevant non-duplicates.")

                if (hypothesis) {
                    hypothesisTrueCount++
                } else {
                    hypothesisFalseCount++
                }
            }
        }
        println()
    }

    println()
    println("Total: $hypothesisTrueCount true, $hypothesisFalseCount false (of ${task.topics.size} total)")

    // Remarks:
    // Relevant near-duplicate popularity > relevant non-duplicate popularity
    // Above assumption does not always hold true.


    // TODO original vs. deduplicated average -> prove hypothesis
}