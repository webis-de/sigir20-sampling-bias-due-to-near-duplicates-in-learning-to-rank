package de.webis.webisstud.thesis.reimer.ltr

import de.webis.webisstud.thesis.reimer.model.Document
import de.webis.webisstud.thesis.reimer.model.Relevance
import de.webis.webisstud.thesis.reimer.model.RunLine

data class JudgedRunLine(
		val runLine: RunLine,
		val relevance: Relevance
) : Document.Metadata {
	override val documentId = runLine.documentId.also { check(it == relevance.documentId) }
	override val topicId = runLine.topicId.also { check(it == relevance.topicId) }
}