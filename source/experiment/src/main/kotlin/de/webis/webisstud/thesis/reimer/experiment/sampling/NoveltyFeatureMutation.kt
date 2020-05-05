package de.webis.webisstud.thesis.reimer.experiment.sampling

import de.webis.webisstud.thesis.reimer.groups.canonical.canonicalFingerprintGroups
import de.webis.webisstud.thesis.reimer.ltr.sampling.Mutation
import de.webis.webisstud.thesis.reimer.ltr.split.Split
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.FeatureVector

object NoveltyFeatureMutation : Mutation {

	override val id = "novelty-feature"

	override fun mutate(items: Iterable<FeatureVector>, split: Split, corpus: Corpus): Iterable<FeatureVector> {
		val groups = corpus.canonicalFingerprintGroups.groups
		return items.map { vector ->
			val group = groups.find { vector.documentId in it.ids }
			val isCanonical = group == null || group.globalCanonical == vector.documentId
			vector.copy(features = vector.features + if (isCanonical) 1f else 0f)
		}
	}
}