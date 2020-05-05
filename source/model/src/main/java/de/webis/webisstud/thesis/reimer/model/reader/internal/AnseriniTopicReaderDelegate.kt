package de.webis.webisstud.thesis.reimer.model.reader.internal

import de.webis.webisstud.thesis.reimer.model.Document
import de.webis.webisstud.thesis.reimer.model.Task
import de.webis.webisstud.thesis.reimer.model.Topic
import de.webis.webisstud.thesis.reimer.model.reader.TopicReader
import java.io.InputStream

class AnseriniTopicReaderDelegate<T> internal constructor(
        private val anseriniReader: AnseriniTopicReader<T>
) : TopicReader<Document, Topic<Document>> {

    override fun read(stream: InputStream, task: Task): List<Topic<Document>> {
        return anseriniReader
                .read(stream.bufferedReader())
                .map { (id: T, data: Map<String, String>) ->
                    val title = data.getValue("title")
                    val description = data["description"]
                    val narrative = data["narrative"]
                    object : Topic<Document>, Map<String, Document> by emptyMap() {
                        override val task = task
                        override val id = id.toString()
                        override val title = title
                        override val description = description
                        override val narrative = narrative
                    }
                }
    }
}