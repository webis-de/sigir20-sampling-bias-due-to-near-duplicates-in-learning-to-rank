package de.webis.webisstud.thesis.reimer.experiment

import de.webis.webisstud.thesis.reimer.data.Data
import de.webis.webisstud.thesis.reimer.ltr.MetricType
import de.webis.webisstud.thesis.reimer.ltr.RankerType
import de.webis.webisstud.thesis.reimer.ltr.sampling.Mutation
import de.webis.webisstud.thesis.reimer.ltr.sampling.Sampling
import de.webis.webisstud.thesis.reimer.ltr.split.FeatureVectorSplitter
import de.webis.webisstud.thesis.reimer.model.Corpus
import java.io.Serializable

data class ExperimentConfiguration(
		val corpus: Corpus,
		val splitter: FeatureVectorSplitter,
		val ranker: RankerType,
		val metric: MetricType,
		val underSampling: Sampling,
		val overSampling: Sampling,
		val featureMutation: Mutation,
		val trial: Int
) : Serializable {

	val experimentDir by lazy {
		Data.newExperimentsDir()
				.resolve(corpus.name.toLowerCase())
				.resolve(splitter.id)
				.resolve(ranker.id)
				.resolve(metric.id)
				.resolve(underSampling.id)
				.resolve(overSampling.id)
				.resolve(featureMutation.id)
				.resolve("trial-$trial")
				.apply { mkdirs() }
	}
}