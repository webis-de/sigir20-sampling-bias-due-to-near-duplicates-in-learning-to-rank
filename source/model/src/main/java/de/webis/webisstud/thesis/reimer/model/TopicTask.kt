package de.webis.webisstud.thesis.reimer.model

interface TopicTask<DocumentType : Document, TopicType : Topic<DocumentType>> :
        Task, Map<String, TopicType>, Iterable<TopicType> {

    fun getTopic(key: String): TopicType = getValue(key)

    val topics: Set<TopicType> get() = toSet()

    override fun iterator(): Iterator<TopicType> {
        return values.iterator()
    }
}