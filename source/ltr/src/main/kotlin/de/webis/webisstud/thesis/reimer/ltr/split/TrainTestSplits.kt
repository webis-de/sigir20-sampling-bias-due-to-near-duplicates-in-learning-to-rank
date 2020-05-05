package de.webis.webisstud.thesis.reimer.ltr.split

import de.webis.webisstud.thesis.reimer.model.Corpus

val Corpus.featureVectorSplitters: Set<FeatureVectorSplitter>
	get() {
		return CorpusTrainTestSplits.values()
				.find { it.corpus == this }
				.let { requireNotNull(it) { "No train/test splitters found for corpus $name." } }
	}