package de.webis.webisstud.thesis.reimer.ltr

import de.webis.webisstud.thesis.reimer.ltr.files.source.MetadataSource
import de.webis.webisstud.thesis.reimer.model.FeatureVector
import de.webis.webisstud.thesis.reimer.model.RunLine
import de.webis.webisstud.thesis.reimer.model.format.RunLineFormat

class RerankedRunSource(
		private val source: TrainingFeatureVectorSource,
		testRuns: Sequence<RunLine>,
		testVectors: Sequence<FeatureVector>

) : MetadataSource<RunLine> {

	override val format = RunLineFormat

	private val sequence by lazy {
		source.reranker.rerank(testRuns, testVectors)
	}

	override fun iterator() = sequence.iterator()
}
