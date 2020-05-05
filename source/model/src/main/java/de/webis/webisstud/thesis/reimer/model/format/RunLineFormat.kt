package de.webis.webisstud.thesis.reimer.model.format

import de.webis.webisstud.thesis.reimer.model.RunLine

object RunLineFormat : StringFormat<RunLine> {

    private const val EMPTY_TAG = "NO_TAG"
    private val DELIMITER = Regex("[\\s\\t]+")

    override fun parse(from: String): RunLine {
        val args = from.split(DELIMITER)
        require(args.size == 6) {
            "Run files must contain exactly 6 columns.\nLine: $from"
        }
        val topicId = args[0]
        val documentId = args[2]
        return RunLine(
                documentId,
                topicId,
                score = args[4].toFloat(),
                position = args[3].toInt(),
                tag = args[5].takeUnless { it == EMPTY_TAG }
        )
    }

    override fun format(from: RunLine) =
            "${from.topicId} Q0 ${from.documentId} ${from.position} ${from.score} ${from.tag ?: EMPTY_TAG}"
}