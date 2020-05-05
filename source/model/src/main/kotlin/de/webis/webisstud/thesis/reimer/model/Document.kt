package de.webis.webisstud.thesis.reimer.model

interface Document : Comparable<Document> {
    val topic: Topic<*>
    val id: String

    override fun compareTo(other: Document) = id.compareTo(other.id)

    fun asMetadata(): Metadata {
        return object : Metadata {
            override val documentId = id
            override val topicId = topic.id
        }
    }

    interface Metadata {
        val documentId: String
        val topicId: String
    }
}
