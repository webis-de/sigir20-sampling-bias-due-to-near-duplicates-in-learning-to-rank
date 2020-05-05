package de.webis.webisstud.thesis.reimer.evaluation.internal

import de.webis.webisstud.thesis.reimer.evaluation.RankingEvaluation
import de.webis.webisstud.thesis.reimer.ltr.JudgedRunLine
import de.webis.webisstud.thesis.reimer.ltr.MetricType
import de.webis.webisstud.thesis.reimer.model.Corpus
import kotlinx.serialization.serializer

internal class MetricEvaluation(
		private val metric: MetricType
) : RankingEvaluation<Double> {

	override val serializer get() = Double.serializer()

	override val id get() = metric.id

	override fun evaluate(run: List<JudgedRunLine>, corpus: Corpus) = metric.score(run)
}
