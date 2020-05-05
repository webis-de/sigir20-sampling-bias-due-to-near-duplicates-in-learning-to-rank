package de.webis.webisstud.thesis.reimer.ltr

import ciir.umass.edu.learning.DataPoint
import de.webis.webisstud.thesis.reimer.model.FeatureVector

internal class FeatureVectorDataPoint(private val vector: FeatureVector) : DataPoint() {

    private companion object {
        fun List<Float>.toFloatArrayExceptFirst(): FloatArray {
            return FloatArray(size).also { array ->
                for (index in indices) {
                    if (index != 0) {
                        array[index] = get(index)
                    }
                }
            }
        }
    }

    private val features = object : AbstractList<Float>() {
        override val size get() = vector.features.size + 1

        override fun get(index: Int): Float {
            if (index == 0) error("Feature ID must start from 1.")
            return vector.features[index - 1].takeUnless(Float::isNaN) ?: 0f
        }
    }

    override fun getID() = vector.topicId

    override fun setID(id: String) {
        error("Feature vector is read-only.")
    }

    override fun getDescription() = "docid = ${vector.documentId}"

    override fun setDescription(description: String?) {
        error("Feature vector is read-only.")
    }

    override fun getLabel() = vector.relevance

    override fun setLabel(label: Float) {
        error("Feature vector is read-only.")
    }

    override fun setFeatureVector(dfVals: FloatArray) {
        error("Feature vector is read-only.")
    }

    override fun getFeatureValue(fid: Int) = features[fid]

    override fun getFeatureVector() = features.toFloatArrayExceptFirst()

    override fun setFeatureValue(fid: Int, fval: Float) {
        error("Feature vector is read-only.")
    }

    override fun getFeatureCount() = vector.features.size
}