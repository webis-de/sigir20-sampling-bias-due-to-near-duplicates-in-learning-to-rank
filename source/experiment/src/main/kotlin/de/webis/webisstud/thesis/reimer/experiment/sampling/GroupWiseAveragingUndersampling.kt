package de.webis.webisstud.thesis.reimer.experiment.sampling

import de.webis.webisstud.thesis.reimer.groups.fingerprintGroups
import de.webis.webisstud.thesis.reimer.ltr.sampling.Sampling
import de.webis.webisstud.thesis.reimer.ltr.split.Split
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.FeatureVector

object GroupWiseAveragingUndersampling : Sampling {

	override val id = "groupwise-averaging"

	override fun sample(items: Collection<FeatureVector>, split: Split, corpus: Corpus): Collection<FeatureVector> {
		// Only undersample training data.
		if (split != Split.Training) return items

		val groups = corpus.fingerprintGroups
				.groups
				.mapNotNull { group ->
					val vectors = items.filter { vector ->
						vector.documentId in group.ids
					}
					if (vectors.isEmpty()) null
					else group to vectors
				}
				.toMap()
		val groupedVectors = groups.values.flatten().toSet()
		val ungroupedVectors = items.filter { it !in groupedVectors }.toSet()

        return ungroupedVectors + groups
                .flatMap { (_, vectors) ->
                    vectors.groupBy { it.topicId }
                            .map { (topic, vectors) ->
                                // TODO Should we select the most relevant document here?
                                // Alternatively, we could search for the "most canonical" document
                                // in our sub-group, and only fall back to "randomly" drawing an alternative,
                                // if there's no canonical document, or if multiple documents are equally canonical.
                                val document = vectors
                                        .map { it.documentId }
                                        .min()!!
                                val features = vectors
                                        .map { it.features }
                                        .componentWiseAverage()
                                val relevance = vectors
                                        .map { it.relevance }
                                        .average()
                                        .toFloat()
                                FeatureVector(document, topic, relevance, features)
                            }
                }
    }

    private fun List<List<Float>>.componentWiseAverage(): List<Float> {
        return first().indices
                .map { index ->
                    map { features ->
                        features[index]
                    }.average().toFloat()
                }
    }
}