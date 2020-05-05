package de.webis.webisstud.thesis.reimer.evaluation.evaluation

import de.webis.webisstud.thesis.reimer.corpus.url.urls
import de.webis.webisstud.thesis.reimer.evaluation.RankingEvaluation
import de.webis.webisstud.thesis.reimer.evaluation.internal.isWikipedia
import de.webis.webisstud.thesis.reimer.ltr.JudgedRunLine
import de.webis.webisstud.thesis.reimer.model.Corpus
import kotlinx.serialization.serializer

object FirstWikipediaRank : RankingEvaluation<Int> {

	override val serializer = Int.serializer()

	override val id = "first-wikipedia-rank"

	override fun evaluate(run: List<JudgedRunLine>, corpus: Corpus): Int {
		val urls = corpus.urls
		return run
				.asSequence()
				.filter { urls.isWikipedia(it.documentId) }
				.map { it.runLine.position }
				.min()
				?: -1 // No Wikipedia documents in this run.
	}
}