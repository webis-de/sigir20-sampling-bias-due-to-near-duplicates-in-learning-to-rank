package de.webis.webisstud.thesis.reimer.evaluation.internal

import de.webis.webisstud.thesis.reimer.evaluation.RankingEvaluation
import de.webis.webisstud.thesis.reimer.ltr.JudgedRunLine
import de.webis.webisstud.thesis.reimer.model.Corpus
import kotlinx.serialization.map
import kotlinx.serialization.serializer

internal class PerTopicNamedEvaluation<T>(
		private val evaluation: RankingEvaluation<T>
) : RankingEvaluation<Map<String, T>> {

	override val id get() = "${evaluation.id}-per-topic-named"

	override val serializer get() = (String.serializer() to evaluation.serializer).map

	override fun evaluate(run: List<JudgedRunLine>, corpus: Corpus): Map<String, T> {
		return run.groupBy { it.topicId }
				.mapValues { (_, topicRun) ->
					evaluation.evaluate(topicRun, corpus)
				}
	}
}
