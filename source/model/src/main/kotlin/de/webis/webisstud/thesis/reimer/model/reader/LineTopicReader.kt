package de.webis.webisstud.thesis.reimer.model.reader

import de.webis.webisstud.thesis.reimer.model.Document
import de.webis.webisstud.thesis.reimer.model.Task
import de.webis.webisstud.thesis.reimer.model.Topic
import java.io.InputStream

object LineTopicReader : TopicReader<Document, Topic<Document>> {
    override fun read(stream: InputStream, task: Task): List<Topic<Document>> {
        return stream
                .bufferedReader()
                .useLines { lines ->
                    lines.map { line ->
                        val id = line.substringBefore(':')
                        val title = line.substringAfter(':')
                        object : Topic<Document>, Map<String, Document> by emptyMap() {
                            override val task = task
                            override val id = id
                            override val title = title
                        }
                    }.toList()
                }
    }
}
