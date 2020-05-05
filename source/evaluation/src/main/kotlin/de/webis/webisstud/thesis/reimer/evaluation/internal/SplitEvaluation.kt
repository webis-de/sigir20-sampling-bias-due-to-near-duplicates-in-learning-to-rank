package de.webis.webisstud.thesis.reimer.evaluation.internal

import de.webis.webisstud.thesis.reimer.evaluation.RankingEvaluation
import de.webis.webisstud.thesis.reimer.ltr.JudgedRunLine
import de.webis.webisstud.thesis.reimer.ltr.split.Split
import de.webis.webisstud.thesis.reimer.model.Corpus

internal class SplitEvaluation<T>(
		private val evaluation: RankingEvaluation<T>,
		private val filterSplits: Set<Split>
) : RankingEvaluation<T> {

	override val splits get() = evaluation.splits.intersect(filterSplits)

	override val serializer get() = evaluation.serializer

	override val id get() = evaluation.id

	override fun evaluate(run: List<JudgedRunLine>, corpus: Corpus) = evaluation.evaluate(run, corpus)
}
