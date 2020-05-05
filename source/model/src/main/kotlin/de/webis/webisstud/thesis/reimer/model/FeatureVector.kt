package de.webis.webisstud.thesis.reimer.model

data class FeatureVector(
        override val documentId: String,
        override val topicId: String,
        val relevance: Float,
        val features: List<Float>
) : Document.Metadata {

    companion object {
        /**
         * Exactly the same value as `ciir.umass.edu.learning.DataPoint.UNKNOWN`.
         */
        val UNKNOWN = Float.NaN
    }


    constructor(
            documentId: String,
            topicId: String,
            relevance: Int,
            vararg features: Float
    ) : this(documentId, topicId, relevance.toFloat(), features.toList())
}
