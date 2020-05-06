package de.webis.webisstud.thesis.reimer.evaluation.evaluation

import de.webis.webisstud.thesis.reimer.corpus.url.urls
import de.webis.webisstud.thesis.reimer.evaluation.RankingEvaluation
import de.webis.webisstud.thesis.reimer.evaluation.internal.isWikipedia
import de.webis.webisstud.thesis.reimer.groups.canonical.canonicalFingerprintGroups
import de.webis.webisstud.thesis.reimer.groups.fingerprintGroups
import de.webis.webisstud.thesis.reimer.ltr.JudgedRunLine
import de.webis.webisstud.thesis.reimer.model.Corpus
import kotlinx.serialization.serializer

object FirstDuplicateRank : RankingEvaluation<Int> {

	override val serializer = Int.serializer()

	override val id = "first-duplicate-rank"

	override fun evaluate(run: List<JudgedRunLine>, corpus: Corpus): Int {
		val groups = corpus.fingerprintGroups
		return run
				.asSequence()
				.filter { groups.anyGroupContains(it.documentId) }
				.map { it.runLine.position }
				.min()
				?: -1 // No Wikipedia documents in this run.
	}
}