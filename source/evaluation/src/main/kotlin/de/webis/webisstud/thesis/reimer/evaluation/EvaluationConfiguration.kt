package de.webis.webisstud.thesis.reimer.evaluation

import de.webis.webisstud.thesis.reimer.data.Data
import de.webis.webisstud.thesis.reimer.ltr.sampling.RunSampling
import java.io.File
import java.io.Serializable
import java.time.Instant

data class EvaluationConfiguration(
		val time: Instant,
		val corpus: String,
		val splitter: String,
		val ranker: String,
		val metric: String,
		val undersampling: String,
		val oversampling: String,
		val featureMutation: String,
		val trial: String,
		val runSampling: RunSampling,
		val evaluation: RankingEvaluation<*>
) : Serializable {

	val experimentDirs: File
		get() {
			return Data.experimentsDir(time)
					.resolve(corpus)
					.resolve(splitter)
					.resolve(ranker)
					.resolve(metric)
					.resolve(undersampling)
					.resolve(oversampling)
					.resolve(featureMutation)
					.resolve(trial)
		}
}
