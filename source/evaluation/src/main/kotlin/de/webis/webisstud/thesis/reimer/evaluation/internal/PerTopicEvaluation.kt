package de.webis.webisstud.thesis.reimer.evaluation.internal

import de.webis.webisstud.thesis.reimer.evaluation.RankingEvaluation
import de.webis.webisstud.thesis.reimer.ltr.JudgedRunLine
import de.webis.webisstud.thesis.reimer.model.Corpus
import kotlinx.serialization.list

internal class PerTopicEvaluation<T>(
		private val evaluation: RankingEvaluation<T>
) : RankingEvaluation<List<T>> {

	override val id get() = "${evaluation.id}-per-topic"

	override val serializer get() = evaluation.serializer.list

	override fun evaluate(run: List<JudgedRunLine>, corpus: Corpus): List<T> {
		return run.groupBy { it.topicId }
				.map { (_, topicRun) ->
					evaluation.evaluate(topicRun, corpus)
				}
	}
}
