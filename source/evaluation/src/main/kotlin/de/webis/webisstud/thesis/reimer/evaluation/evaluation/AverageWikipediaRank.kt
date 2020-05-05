package de.webis.webisstud.thesis.reimer.evaluation.evaluation

import de.webis.webisstud.thesis.reimer.corpus.url.urls
import de.webis.webisstud.thesis.reimer.evaluation.RankingEvaluation
import de.webis.webisstud.thesis.reimer.evaluation.internal.isWikipedia
import de.webis.webisstud.thesis.reimer.ltr.JudgedRunLine
import de.webis.webisstud.thesis.reimer.model.Corpus
import dev.reimer.kotlin.jvm.ktx.takeUnlessNaN
import kotlinx.serialization.serializer

object AverageWikipediaRank : RankingEvaluation<Double> {

	override val serializer = Double.serializer()

	override val id = "average-wikipedia-rank"

	override fun evaluate(run: List<JudgedRunLine>, corpus: Corpus): Double {
		val urls = corpus.urls
		return run
				.asSequence()
				.filter { urls.isWikipedia(it.documentId) }
				.map { it.runLine.position }
				.average()
				.takeUnlessNaN()
				?: -1.0 // No Wikipedia documents in this run.

	}
}