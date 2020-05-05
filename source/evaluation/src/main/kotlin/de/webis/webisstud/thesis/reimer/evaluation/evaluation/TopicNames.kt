package de.webis.webisstud.thesis.reimer.evaluation.evaluation

import de.webis.webisstud.thesis.reimer.evaluation.RankingEvaluation
import de.webis.webisstud.thesis.reimer.ltr.JudgedRunLine
import de.webis.webisstud.thesis.reimer.model.Corpus
import kotlinx.serialization.list
import kotlinx.serialization.serializer

object TopicNames : RankingEvaluation<List<String>> {

	override val serializer = String.serializer().list

	override val id = "topic-names"

	override fun evaluate(run: List<JudgedRunLine>, corpus: Corpus) =
			run.map(JudgedRunLine::topicId).distinct()
}