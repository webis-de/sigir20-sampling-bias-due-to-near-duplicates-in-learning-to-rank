package de.webis.webisstud.thesis.reimer.ltr.pipeline

import de.webis.webisstud.thesis.reimer.model.FeatureVector
import de.webis.webisstud.thesis.reimer.model.RunLine
import de.webis.webisstud.thesis.reimer.model.format.RunLineFormat

interface Reranker {
	fun rerank(testRuns: Sequence<RunLine>, testVectors: Sequence<FeatureVector>): Sequence<RunLine>

    fun rerankFeatureVectors(featureVectors: Sequence<FeatureVector>): Sequence<String> {
        val runs = featureVectors
                .mapIndexed { index, vector ->
	                RunLine(vector.documentId, vector.topicId, 0f, index + 1)
                }
                .toList()

        return rerank(runs.asSequence(), featureVectors.asSequence())
                .map { RunLineFormat.format(it) }
                .asSequence()
    }
}