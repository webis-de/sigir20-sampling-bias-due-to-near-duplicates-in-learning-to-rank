package de.webis.webisstud.thesis.reimer.ltr.sampling

import de.webis.webisstud.thesis.reimer.ltr.split.Split
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.FeatureVector
import java.io.Serializable

/**
 * Mutation strategy for modifying features for training and/or test splits.
 */
interface Mutation : Serializable {
	val id: String

	fun mutate(items: Iterable<FeatureVector>, split: Split, corpus: Corpus): Iterable<FeatureVector>
}