package de.webis.webisstud.thesis.reimer.data

import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.Document
import de.webis.webisstud.thesis.reimer.model.Task
import de.webis.webisstud.thesis.reimer.model.Topic
import java.io.File

val Corpus.dataDir get() = Data.corporaDir.resolve(name.toLowerCase())

val Task.dataDir
    get() = Data.tasksDir.resolve(id.toLowerCase().replace(' ', '-'))

private val Task.topicsDir get() = dataDir.resolve("topics")

val Topic<*>.dataDir get() = task.topicsDir.resolve(id)
val <DocumentType : Document> Iterable<Topic<DocumentType>>.dataDir: File
    get() {
        return sorted().run {
            require(isNotEmpty())
            val ids = joinToString(separator = "-") { it.id }
            first().task.topicsDir.resolve(ids)
        }
    }

val Topic<*>.files get() = TopicFiles(this)

val <DocumentType : Document> Iterable<Topic<DocumentType>>.files get() = TopicFiles(this)
