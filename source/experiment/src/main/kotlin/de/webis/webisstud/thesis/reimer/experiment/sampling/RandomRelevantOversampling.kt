package de.webis.webisstud.thesis.reimer.experiment.sampling

import de.webis.webisstud.thesis.reimer.ltr.sampling.Sampling
import de.webis.webisstud.thesis.reimer.ltr.split.Split
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.FeatureVector
import kotlin.random.Random

object RandomRelevantOversampling : Sampling {

	private val RANDOM = Random

	override val id = "random-relevant"

	override fun sample(items: Collection<FeatureVector>, split: Split, corpus: Corpus): Collection<FeatureVector> {
		// Only oversample training data.
		if (split != Split.Training) return items

		val irrelevant = items.filter { it.relevance <= 0 }
		val relevant = items.filter { it.relevance > 0 }
		val diff = irrelevant.size - relevant.size

		if (relevant.isEmpty() || diff <= 0) return items

		val retainedItems = items.toMutableList()
		repeat(diff) {
			retainedItems.add(relevant.random(RANDOM))
		}

		return retainedItems
	}
}