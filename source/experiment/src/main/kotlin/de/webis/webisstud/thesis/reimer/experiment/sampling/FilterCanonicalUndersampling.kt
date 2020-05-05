package de.webis.webisstud.thesis.reimer.experiment.sampling

import de.webis.webisstud.thesis.reimer.experiment.canonicalFingerprintGroupsContainsWikipedia
import de.webis.webisstud.thesis.reimer.ltr.sampling.Sampling
import de.webis.webisstud.thesis.reimer.ltr.split.Split
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.FeatureVector

object FilterCanonicalUndersampling : Sampling {

	override val id = "filter-canonical"

	override fun sample(items: Collection<FeatureVector>, split: Split, corpus: Corpus): Collection<FeatureVector> {
		// Only undersample training data.
		if (split != Split.Training) return items

		val groups = corpus.canonicalFingerprintGroupsContainsWikipedia.groups
		return items.filter { vector ->
			val group = groups.find { vector.documentId in it.ids }
			group == null || group.globalCanonical == vector.documentId
		}
	}
}