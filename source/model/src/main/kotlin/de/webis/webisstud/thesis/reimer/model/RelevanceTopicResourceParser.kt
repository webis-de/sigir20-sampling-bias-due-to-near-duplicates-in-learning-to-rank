package de.webis.webisstud.thesis.reimer.model

import de.webis.webisstud.thesis.reimer.model.reader.RelevanceReader
import de.webis.webisstud.thesis.reimer.model.reader.TopicReader

class RelevanceTopicResourceParser(
        val task: RelevanceTopicResourceTask<JudgedDocument, JudgedTopic<JudgedDocument>>,
        private val topicReader: TopicReader<*, *>, // TODO Specify type parameter.
        private val relevanceReader: RelevanceReader
) : RelevanceTopicResourceTask<JudgedDocument, JudgedTopic<JudgedDocument>>, AbstractMap<String, JudgedTopic<JudgedDocument>>() {

    override val id get() = task.id
    override val corpus get() = task.corpus
    override val relevanceResource get() = task.relevanceResource
    override val topicResource get() = task.topicResource

    private class MutableTopic(
            override val task: Task,
            override val id: String,
            override val title: String,
            override val description: String?,
            override val narrative: String?
    ) : JudgedTopic<JudgedDocument>, MutableMap<String, JudgedDocument> by mutableMapOf()

    private data class Document(
            override val topic: Topic<*>,
            override val id: String,
            override val judgement: Int
    ) : JudgedDocument

    private fun parse(): Map<String, JudgedTopic<JudgedDocument>> {
        val topics = topicReader
                .read(topicResource.inputStream(), this)
                .map { topicData ->
                    val id = topicData.id
                    val topic = MutableTopic(
                            this,
                            id,
                            topicData.title,
                            topicData.description,
                            topicData.narrative
                    )
                    id to topic
                }
                .toMap()

        relevanceReader
                .read(relevanceResource.inputStream())
                .forEach { relevance ->
                    val topic = topics.getValue(relevance.topicId)
                    val id = relevance.documentId
                    topic[id] = Document(topic, id, relevance.judgement)
                }

        return topics
    }

    override val entries: Set<Map.Entry<String, JudgedTopic<JudgedDocument>>> by lazy { parse().entries }
}