package de.webis.webisstud.thesis.reimer.evaluation.evaluation

import de.webis.webisstud.thesis.reimer.evaluation.RankingEvaluation
import de.webis.webisstud.thesis.reimer.ltr.JudgedRunLine
import de.webis.webisstud.thesis.reimer.model.Corpus
import kotlinx.serialization.serializer

object RunSize : RankingEvaluation<Int> {

	override val serializer = Int.serializer()

	override val id = "run-size"

	override fun evaluate(run: List<JudgedRunLine>, corpus: Corpus) = run.count()
}