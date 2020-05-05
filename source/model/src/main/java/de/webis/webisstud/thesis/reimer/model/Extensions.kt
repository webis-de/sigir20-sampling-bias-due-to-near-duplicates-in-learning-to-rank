package de.webis.webisstud.thesis.reimer.model

@Suppress("unused")
inline fun <reified T> T.resource(name: String): Resource {
    return Resource(name, T::class)
}

val Corpus.trecTasks
    get() = TrecTask.values().asSequence().filter { it.corpus == this }

val Corpus.trecTopics: Sequence<JudgedTopic<JudgedDocument>>
    get() = trecTasks.flatMap { it.topics.asSequence() }

val Corpus.trecDocuments: Sequence<JudgedDocument>
    get() {
        return trecTopics
                .flatMap { topic ->
                    topic.documents.asSequence()
                }
    }