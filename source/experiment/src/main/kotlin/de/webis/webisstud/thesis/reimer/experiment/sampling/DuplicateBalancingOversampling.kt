package de.webis.webisstud.thesis.reimer.experiment.sampling

import de.webis.webisstud.thesis.reimer.groups.FingerprintGroup
import de.webis.webisstud.thesis.reimer.groups.fingerprintGroups
import de.webis.webisstud.thesis.reimer.ltr.files.source.featureVectorSource
import de.webis.webisstud.thesis.reimer.ltr.sampling.Sampling
import de.webis.webisstud.thesis.reimer.ltr.sampling.sample
import de.webis.webisstud.thesis.reimer.ltr.split.Split
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.FeatureVector
import de.webis.webisstud.thesis.reimer.model.trecTopics
import dev.reimer.kotlin.jvm.ktx.lcm

/**
 * This form of theoretically fair oversampling won't work,
 * as the number of duplicates we must create is immense.
 */
object DuplicateBalancingOversampling : Sampling {

	override val id = "balance-group-size"

	override fun sample(items: Collection<FeatureVector>, split: Split, corpus: Corpus): Collection<FeatureVector> {
		// Only oversample training data.
		if (split != Split.Training) return items

		val ids = items.map { it.documentId }
		val groups = corpus.fingerprintGroups
				.groups
				.filter { group -> group.any { id -> id in ids } }
		val groupSizeLCM = groups
				.map(FingerprintGroup::size)
				.map(Int::toLong)
				.reduce { a, b ->
					println("$a lcm $b")
					a lcm b
				}
		print("LCM group size: $groupSizeLCM")
		var fvCount = 0.toBigInteger()
		return items.flatMap { vector ->
			val group = groups.find { group -> vector.documentId in group.ids }
			val groupSize = group?.size ?: 1
			val duplicationFactor = groupSizeLCM / groupSize
			fvCount += duplicationFactor.toBigInteger()
			println(fvCount)
			(1..duplicationFactor).map { vector }
		}
	}


	@JvmStatic
	fun main(args: Array<String>) {
		val corpus = Corpus.ClueWeb09
		val featureVectors = corpus.featureVectorSource
		val topics = corpus.trecTopics
				.toList().shuffled()
				.take(10)
				.map { it.id }
		featureVectors
				.filter { it.topicId in topics }
				.toList()
				.sample(DuplicateBalancingOversampling, Split.Training, corpus)
	}
}

