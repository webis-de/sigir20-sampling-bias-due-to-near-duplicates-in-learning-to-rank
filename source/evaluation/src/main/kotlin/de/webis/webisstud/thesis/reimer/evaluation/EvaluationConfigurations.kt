package de.webis.webisstud.thesis.reimer.evaluation

import de.webis.webisstud.thesis.reimer.data.Data
import de.webis.webisstud.thesis.reimer.evaluation.evaluation.*
import de.webis.webisstud.thesis.reimer.evaluation.sampling.DuplicatesIrrelevantRunSampling
import de.webis.webisstud.thesis.reimer.evaluation.sampling.RemoveDuplicatesRunUndersampling
import de.webis.webisstud.thesis.reimer.ltr.Identity
import de.webis.webisstud.thesis.reimer.ltr.MetricType
import de.webis.webisstud.thesis.reimer.ltr.split.Split
import java.io.File
import java.time.Instant

class EvaluationConfigurations(
		private val time: Instant,
		val debug: Boolean = false
) {
	private val baseDir = Data.experimentsDir(time)
	val resultFile = baseDir.resolve("evaluation-of-experiments.jsonl")

	internal companion object {

		internal val runSamplings = arrayOf(
				RemoveDuplicatesRunUndersampling,
				DuplicatesIrrelevantRunSampling,
				Identity
		)

		internal val rankingEvaluations = arrayOf(
				FirstDuplicateRank.perTopic().onSplits(Split.Test),
				FirstIrrelevantDuplicateRank.perTopic().onSplits(Split.Test),
				FirstWikipediaRank.perTopic().onSplits(Split.Test),
				FirstIrrelevantWikipediaRank.perTopic().onSplits(Split.Test),
//				TopicNames.onSplits(Split.Test),
//				AverageWikipediaRank.perTopic().onSplits(Split.Test),
//				SubListDuplicateCount.perTopic().onSplits(Split.Test),
				DomainFairness.perTopic().onSplits(Split.Test),
//				RunSize,
//				MetricType.Map.asEvaluation().perTopic().onSplits(Split.Test),
//				MetricType.Ndcg.asEvaluation().perTopic().onSplits(Split.Test),
				MetricType.Ndcg(10).asEvaluation().perTopic().onSplits(Split.Test),
				MetricType.Ndcg(20).asEvaluation().perTopic().onSplits(Split.Test)
		)

	}

	private fun <T> Sequence<T>.debugTake(n: Int) =
			if (debug) take(n) else this

	private fun File.listDirectories(): Sequence<File> =
			listFiles { file: File -> file.isDirectory }?.asSequence() ?: emptySequence()

	val configurations: Sequence<EvaluationConfiguration>
		get() {
			return baseDir.listDirectories().flatMap { corpus: File ->
				corpus.listDirectories().flatMap { splitter: File ->
					splitter.listDirectories().flatMap { ranker: File ->
						ranker.listDirectories().flatMap { metric: File ->
							metric.listDirectories().flatMap { underSampling: File ->
								underSampling.listDirectories().flatMap { overSampling: File ->
									overSampling.listDirectories().flatMap { featureMutation: File ->
										featureMutation.listDirectories().flatMap { trial: File ->
											runSamplings.asSequence().flatMap { runSampling ->
												rankingEvaluations.asSequence().map { evaluation ->
													EvaluationConfiguration(
															time,
															corpus.name,
															splitter.name,
															ranker.name,
															metric.name,
															underSampling.name,
															overSampling.name,
															featureMutation.name,
															trial.name,
															runSampling,
															evaluation
													)
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}.debugTake(50)
		}
}