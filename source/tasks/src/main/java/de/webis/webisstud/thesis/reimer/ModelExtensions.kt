package de.webis.webisstud.thesis.reimer

import de.webis.webisstud.thesis.reimer.corpus.url.urls
import de.webis.webisstud.thesis.reimer.model.Corpus.Companion.CLUE_WEB_12_ALEXA_DATE
import de.webis.webisstud.thesis.reimer.model.Document
import de.webis.webisstud.thesis.reimer.model.Topic
import dev.reimer.alexa.AlexaTopRanks
import dev.reimer.alexa.get
import dev.reimer.domain.ktx.Domain
import java.net.URL

fun <T : Document> Topic<T>.averageAlexaRank(
		urls: Map<String, URL> = this.urls,
		alexaRanks: Map<Domain, Long> = AlexaTopRanks.getBlocking(CLUE_WEB_12_ALEXA_DATE),
		predicate: (T) -> Boolean
): Double {
    return documents
		    .asSequence()
		    .filter(predicate)
		    .map { urls[it.id] }
		    .map { url ->
			    if (url != null)
				    alexaRanks[url]
			    else null
		    }
            .map {
                // Assume an Alexa rank of 1000000+1 (one worse than the worst known rank)
                // if documents are not found in top 1M.
                it ?: 1000001
            }
            .average()
}