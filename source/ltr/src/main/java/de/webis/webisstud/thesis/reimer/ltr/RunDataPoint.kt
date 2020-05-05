package de.webis.webisstud.thesis.reimer.ltr

import ciir.umass.edu.learning.DataPoint

internal class RunDataPoint(private val runLine: JudgedRunLine) : DataPoint() {

	override fun getID() = runLine.topicId

	override fun setID(id: String) {
		error("Run line is read-only.")
	}

	override fun getDescription() = "docid = ${runLine.documentId}"

	override fun setDescription(description: String?) {
		error("Run line is read-only.")
	}

	override fun getLabel() = runLine.relevance.judgement.toFloat()

	override fun setLabel(label: Float) {
		error("Run line is read-only.")
	}

	override fun setFeatureVector(dfVals: FloatArray) {
		error("Run line is read-only.")
	}

	override fun getFeatureValue(fid: Int): Float {
		error("Run lines don't have features.")
	}

	override fun getFeatureVector() = floatArrayOf()

	override fun setFeatureValue(fid: Int, fval: Float) {
		error("Run line is read-only.")
	}

	override fun getFeatureCount() = 0
}