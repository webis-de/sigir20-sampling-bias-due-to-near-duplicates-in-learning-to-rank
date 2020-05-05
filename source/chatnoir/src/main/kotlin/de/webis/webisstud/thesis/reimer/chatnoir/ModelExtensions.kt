package de.webis.webisstud.thesis.reimer.chatnoir

import de.webis.WebisUUID
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.Document
import java.net.URL

fun Corpus.chatNoirPermanentUrl(documentId: String): URL {
    val prefix = when (this) {
        Corpus.ClueWeb09,
        Corpus.ClueWeb12 -> name.toLowerCase()
        else -> error("This document's corpus is not supported in ChatNoir")
    }
    val index = when (this) {
        Corpus.ClueWeb09 -> "cw09"
        Corpus.ClueWeb12 -> "cw12"
        else -> error("This document's corpus is not supported in ChatNoir")
    }
    return URL("https://chatnoir.eu/cache?uuid=${WebisUUID(prefix).generateUUID(documentId)}&index=$index&raw")
}

val Document.chatNoirPermanentUrl
    get() = topic.task.corpus.chatNoirPermanentUrl(id)