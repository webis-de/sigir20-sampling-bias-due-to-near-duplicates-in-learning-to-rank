package de.webis.webisstud.thesis.reimer.model.format

import de.webis.webisstud.thesis.reimer.model.Relevance

object FullrelLineFormat : StringFormat<Relevance> {

    private val DELIMITER = Regex("[\\s\\t]+")

    override fun parse(from: String): Relevance {
        val args = from.split(DELIMITER)
        require(args.size == 7) {
            "Fullrels must contain exactly 7 columns.\nLine: $from"
        }
        // Negative judgements lead to issues in LTR algorithms.
        val judgement = args[2]
                .toInt()
                .coerceAtLeast(0)
        val topicId = args[0]
        val documentId = args[1]
        return Relevance(documentId, topicId, judgement)
    }

    override fun format(from: Relevance): String {
        return "${from.topicId} ${from.documentId} ${from.judgement} -1 0 0 0 1"
    }
}