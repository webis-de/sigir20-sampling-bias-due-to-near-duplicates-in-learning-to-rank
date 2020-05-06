package de.webis.webisstud.thesis.reimer.model.format

import de.webis.webisstud.thesis.reimer.model.FeatureVector
import java.util.*

object FeatureVectorLineFormat : StringFormat<FeatureVector> {

    enum class Option {
        FixedPrecision,
        FloatRelevance
    }

    private val OPTIONS = mutableSetOf(Option.FloatRelevance)

    fun setOption(option: Option, enabled: Boolean) {
        if (enabled) OPTIONS.add(option)
        else OPTIONS.remove(option)
    }

    private const val COMMENT_DELIMITER = " # "
    private val COMMENT_DELIMITER_REGEX = Regex("\\s*#\\s*")
    private const val FEATURES_DELIMITER = " "
    private const val FEATURE_DELIMITER = ":"
    private const val TOPIC_ID_PREFIX = "qid$FEATURE_DELIMITER"
    private val TOPIC_ID_REGEX = Regex("$TOPIC_ID_PREFIX([^ ]+)")
    private const val DOCUMENT_ID_PREFIX = "docid = "
    private val DOCUMENT_ID_REGEX = Regex("$DOCUMENT_ID_PREFIX([^ ]+)")

    override fun parse(from: String): FeatureVector {
        val data = from
                .substringBefore(COMMENT_DELIMITER_REGEX)
                .split(FEATURES_DELIMITER)
        val comment = from.substringAfter(COMMENT_DELIMITER_REGEX)

        val topicId = requireNotNull(TOPIC_ID_REGEX.find(data[1])) {  }.groupValues[1]
        val documentId = requireNotNull(DOCUMENT_ID_REGEX.find(comment)).groupValues[1]
        val relevance = data[0].toFloat()
        val features = data
                .asSequence()
                .drop(2)
                .map { it.substringAfter(FEATURE_DELIMITER) }
                .map(String::toFloat)
                .toList()

        return FeatureVector(
                documentId,
                topicId,
                relevance,
                features
        )
    }

    override fun format(from: FeatureVector): String {
        val data = listOf(
                if (Option.FloatRelevance in OPTIONS) from.relevance else from.relevance.toInt(),
                "$TOPIC_ID_PREFIX${from.topicId}",
                *from.features
                        .mapIndexed { index, feature ->
                            "${index + 1}$FEATURE_DELIMITER${if (Option.FixedPrecision in OPTIONS) "%.6f".format(Locale.US, feature) else feature.toString()}"
                        }
                        .toTypedArray()
        )
        val dataString = data.joinToString(FEATURES_DELIMITER)
        // The space, directly following the document ID,
        // is needed for Anserini to correctly extract the document ID.
        val comment = "$DOCUMENT_ID_PREFIX${from.documentId} "
        return dataString + COMMENT_DELIMITER + comment
    }
}