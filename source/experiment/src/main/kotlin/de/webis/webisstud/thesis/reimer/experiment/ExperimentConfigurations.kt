package de.webis.webisstud.thesis.reimer.experiment

import de.webis.webisstud.thesis.reimer.experiment.sampling.FilterCanonicalUndersampling
import de.webis.webisstud.thesis.reimer.experiment.sampling.NoveltyFeatureMutation
import de.webis.webisstud.thesis.reimer.experiment.sampling.NoveltyRelevanceFeedbackMutation
import de.webis.webisstud.thesis.reimer.ltr.Identity
import de.webis.webisstud.thesis.reimer.ltr.MetricType
import de.webis.webisstud.thesis.reimer.ltr.RankerType
import de.webis.webisstud.thesis.reimer.ltr.sampling.plus
import de.webis.webisstud.thesis.reimer.ltr.split.featureVectorSplitters
import de.webis.webisstud.thesis.reimer.model.Corpus

class ExperimentConfigurations(
		private val corpus: Corpus,
		val debug: Boolean = false
) {

	internal companion object {

		internal val rankers = arrayOf(
				RankerType.AdaRank,
				RankerType.CoorAscent,
				RankerType.LambdaMart,
				RankerType.ListNet,
				RankerType.RankBoost,
				RankerType.RankNet,
				RankerType.LinearRegression
		)

		internal val metrics = arrayOf(
//				MetricType.Ndcg,
				MetricType.Ndcg(10),
				MetricType.Ndcg(20)
		)

		internal val undersamplings = arrayOf(
				FilterCanonicalUndersampling,
//				GroupWiseAveragingUndersampling,
				Identity
		)

		internal val oversamplings = arrayOf(
//				RandomRelevantOversampling,
				Identity
		)

		internal val featureMutations = arrayOf(
//				NoveltyRelevanceFeedbackMutation.Move,
//				NoveltyRelevanceFeedbackMutation.Move + NoveltyFeatureMutation,
//				NoveltyRelevanceFeedbackMutation.Scale,
				NoveltyRelevanceFeedbackMutation.Scale + NoveltyFeatureMutation,
//				NoveltyRelevanceFeedbackMutation.Null,
				NoveltyRelevanceFeedbackMutation.Null + NoveltyFeatureMutation,
				Identity
		)

		private const val retries = 5

	}

	private fun <T> Array<out T>.debugTakeLast(n: Int = 1) =
			if (debug) takeLast(n) else toList()

	val configurations: Set<ExperimentConfiguration>
		get() {
			val rankers = arrayOf(
					RankerType.Bm25(corpus),
					*rankers.debugTakeLast().toTypedArray()
			)
			return corpus.featureVectorSplitters.flatMap { split ->
				rankers.flatMap { ranker ->
					metrics.debugTakeLast().flatMap { metric ->
						oversamplings.debugTakeLast().flatMap { overSampling ->
							undersamplings.debugTakeLast().flatMap { underSampling ->
								featureMutations.debugTakeLast().flatMap { featureMutation ->
									List(if (!debug) retries else 1) { trial ->
										ExperimentConfiguration(
												corpus,
												split,
												ranker,
												metric,
												underSampling,
												overSampling,
												featureMutation,
												trial
										)
									}
								}
							}
						}
					}
				}
			}.toSet()
		}
}