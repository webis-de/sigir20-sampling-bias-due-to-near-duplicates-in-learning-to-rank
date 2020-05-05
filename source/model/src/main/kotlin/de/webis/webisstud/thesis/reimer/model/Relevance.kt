package de.webis.webisstud.thesis.reimer.model

data class Relevance(
        override val documentId: String,
        override val topicId: String,
        val judgement: Int
) : Document.Metadata
