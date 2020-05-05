package de.webis.webisstud.thesis.reimer.ltr.pipeline

import ciir.umass.edu.learning.Ranker
import de.webis.webisstud.thesis.reimer.ltr.asDataPoint
import de.webis.webisstud.thesis.reimer.model.FeatureVector
import de.webis.webisstud.thesis.reimer.model.RunLine
import java.io.File

abstract class RankLibReranker : Reranker {

    protected abstract val ranker: Ranker

	override fun rerank(testRuns: Sequence<RunLine>, testVectors: Sequence<FeatureVector>): Sequence<RunLine> {
		val featureVectors = testVectors
				.groupBy { it.topicId }
				.mapValues { (_, vectors) ->
					vectors.map { it.documentId to it }.toMap()
				}
		return testRuns
				.groupBy { it.topicId }
				.asSequence()
				.flatMap { (_, runs) ->
					runs.asSequence()
							.map { run ->
								val dataPoint = featureVectors
										.getValue(run.topicId)
										.getValue(run.documentId)
										.asDataPoint()
								val score = ranker.eval(dataPoint).toFloat()
								score to run
							}
							.sortedByDescending { (score, _) -> score }
							.mapIndexed { index, (score, run) ->
								run.copy(score = score, position = index + 1, tag = "reranked")
							}
                }
    }

    fun save(model: File) = ranker.save(model.path)
}