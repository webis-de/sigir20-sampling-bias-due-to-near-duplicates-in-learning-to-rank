package de.webis.webisstud.thesis.reimer.model.reader

import de.webis.webisstud.thesis.reimer.model.Document
import de.webis.webisstud.thesis.reimer.model.Task
import de.webis.webisstud.thesis.reimer.model.Topic
import java.io.InputStream

interface TopicReader<DocumentType : Document, TopicType : Topic<DocumentType>> {
    fun read(stream: InputStream, task: Task): List<TopicType>
}
