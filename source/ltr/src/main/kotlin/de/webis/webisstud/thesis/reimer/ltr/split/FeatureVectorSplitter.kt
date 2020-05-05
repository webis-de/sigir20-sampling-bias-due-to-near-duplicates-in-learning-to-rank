package de.webis.webisstud.thesis.reimer.ltr.split

import de.webis.webisstud.thesis.reimer.model.Document
import java.io.Serializable

interface FeatureVectorSplitter : Serializable {
	val id: String

	fun getSplit(metadata: Document.Metadata): Split?

	fun getSplits(metadata: Set<Document.Metadata>) =
			metadata.asSequence().map { it to getSplit(it) }.toMap()
}