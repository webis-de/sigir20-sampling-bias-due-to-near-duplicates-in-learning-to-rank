package de.webis.webisstud.thesis.reimer.model

interface JudgedDocument : Document {
    val judgement: Int
    val isRelevant: Boolean get() = judgement > 0
    val isIrrelevant: Boolean get() = !isRelevant
    val relevance get() = Relevance(id, topic.id, judgement)
}