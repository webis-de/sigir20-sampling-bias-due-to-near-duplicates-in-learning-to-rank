package de.webis.webisstud.thesis.reimer.corpus.url

import de.webis.webisstud.thesis.reimer.model.*
import java.net.URL

val Corpus.urls: Map<String, URL>
    get() = requireNotNull(CorpusUrls.values().find { it.corpus == this }) {
        "No hostnames for corpus $this."
    }

val Task.urls: Map<String, URL>
    get() {
        val urls = corpus.urls
        if (this is TopicTask<*, *>) {
            val documents: Set<String> = topics.flatMapTo(mutableSetOf()) { topic ->
                topic.documents.mapTo(mutableSetOf(), Document::id)
            }
            return urls.filter { (id, _) -> id in documents }
        }
        return urls
    }

val Topic<*>.urls: Map<String, URL>
    get() {
        val documents: Set<String> = documents.mapTo(mutableSetOf(), Document::id)
        return task.corpus.urls.filter { (id, _) -> id in documents }
    }

val Document.url: URL get() = topic.task.corpus.urls.getValue(id)
