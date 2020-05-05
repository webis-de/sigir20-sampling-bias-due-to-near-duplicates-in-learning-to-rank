package de.webis.webisstud.thesis.reimer.ltr.sampling

import de.webis.webisstud.thesis.reimer.ltr.split.Split
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.FeatureVector
import java.io.Serializable

/**
 * Sampling strategy for under- or oversampling features for training and/or test splits.
 */
interface Sampling : Serializable {
    val id: String

    fun sample(items: Collection<FeatureVector>, split: Split, corpus: Corpus): Collection<FeatureVector>
}