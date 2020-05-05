package de.webis.webisstud.thesis.reimer.evaluation.evaluation

import de.webis.webisstud.thesis.reimer.corpus.url.urls
import de.webis.webisstud.thesis.reimer.evaluation.internal.getDomainRootOrHostname
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.Document
import dev.reimer.kotlin.jvm.ktx.mapToMap

object DomainFairness : GroupFairnessRankingEvaluation<String?>() {

	override val id = "domain-fairness"

	override fun buildGroups(metadata: Set<Document.Metadata>, corpus: Corpus): Map<Document.Metadata, String?> {
		val urls = corpus.urls
		return metadata.mapToMap {
			it to urls.getDomainRootOrHostname(it.documentId)
		}
	}
}