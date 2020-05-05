package de.webis.webisstud.thesis.reimer.ltr.files.qrel

import de.webis.webisstud.thesis.reimer.ltr.files.source.MetadataSource
import de.webis.webisstud.thesis.reimer.model.Relevance
import de.webis.webisstud.thesis.reimer.model.TrecTask
import de.webis.webisstud.thesis.reimer.model.format.QrelLineFormat

class QrelSource(
        private val task: TrecTask
) : MetadataSource<Relevance> {

    override val format = QrelLineFormat

    private val sequence by lazy {
        println("Load qrels <- task '${task.title}' judgements") // TODO
        task.topics
                .asSequence()
                .flatMap { it.documents.asSequence() }
                .map { it.relevance }
    }

    override fun iterator() = sequence.iterator()
}