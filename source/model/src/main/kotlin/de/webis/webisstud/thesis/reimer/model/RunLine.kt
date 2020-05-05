package de.webis.webisstud.thesis.reimer.model

data class RunLine(
		override val documentId: String,
		override val topicId: String,
		val score: Float,
		val position: Int,
		val tag: String? = null
) : Document.Metadata
