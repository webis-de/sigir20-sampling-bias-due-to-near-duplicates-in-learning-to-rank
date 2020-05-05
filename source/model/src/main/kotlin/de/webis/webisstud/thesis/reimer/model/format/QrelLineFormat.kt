package de.webis.webisstud.thesis.reimer.model.format

import de.webis.webisstud.thesis.reimer.model.Relevance

object QrelLineFormat : StringFormat<Relevance> {

    private val DELIMITER = Regex("[\\s\\t]+")

    override fun parse(from: String): Relevance {
        val args = from.split(DELIMITER)
        require(args.size == 4) {
            "Qrels must contain exactly 4 columns.\nLine: $from"
        }
        // Negative judgements lead to issues in LTR algorithms.
        val judgement = args[3]
                .toInt()
                .coerceAtLeast(0)
        val topicId = args[0]
        val documentId = args[2]
        return Relevance(documentId, topicId, judgement)
    }

    override fun format(from: Relevance): String {
        return "${from.topicId} 0 ${from.documentId} ${from.judgement}"
    }
}