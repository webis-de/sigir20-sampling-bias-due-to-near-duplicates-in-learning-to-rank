package de.webis.webisstud.thesis.reimer.experiment.sampling

import de.webis.webisstud.thesis.reimer.groups.canonical.canonicalFingerprintGroups
import de.webis.webisstud.thesis.reimer.ltr.sampling.Mutation
import de.webis.webisstud.thesis.reimer.ltr.split.Split
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.FeatureVector
import de.webis.webisstud.thesis.reimer.model.trecDocuments
import dev.reimer.kotlin.jvm.ktx.maxDelta

enum class NoveltyRelevanceFeedbackMutation : Mutation {
	/**
	 * Move relevant originals up in the ranking.
	 */
	Move,

	/**
	 * Downscale relevant copies by a fixed factor of `0.1f`.
	 */
	Scale,

	/**
	 * Mark relevant copies irrelevant (judge `0f`).
	 */
	Null;

	override val id = "novelty-relevance-feedback-${name.toLowerCase()}"

	private companion object {
		private const val SCALE = 0.1f // TODO: Determine factor more sophisticated.
	}

	override fun mutate(items: Iterable<FeatureVector>, split: Split, corpus: Corpus): Iterable<FeatureVector> {
		val groups = corpus
				.canonicalFingerprintGroups
				.groups
		val maxRelevanceDelta = corpus
				.trecDocuments
				.map { it.judgement.toFloat() }
				.maxDelta() ?: 0f
		return items.map { vector ->
			val group = groups.find { vector.documentId in it.ids }
			val isCanonical = group == null || group.globalCanonical == vector.documentId

			val relevance = when (this) {
				Move -> when {
					isCanonical && vector.relevance > 0 -> vector.relevance + maxRelevanceDelta
					vector.relevance > 0 -> vector.relevance
					else -> vector.relevance
				}
				Scale -> when {
					isCanonical && vector.relevance > 0 -> vector.relevance
					vector.relevance > 0 -> vector.relevance * SCALE
					else -> vector.relevance
				}
				Null -> when {
					isCanonical -> vector.relevance
					else -> 0f
				}
			}

			vector.copy(
					relevance = relevance
			)
        }
    }
}