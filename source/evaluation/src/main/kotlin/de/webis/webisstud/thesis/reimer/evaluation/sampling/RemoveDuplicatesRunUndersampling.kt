package de.webis.webisstud.thesis.reimer.evaluation.sampling

import de.webis.webisstud.thesis.reimer.groups.FingerprintGroup
import de.webis.webisstud.thesis.reimer.groups.fingerprintGroups
import de.webis.webisstud.thesis.reimer.ltr.JudgedRunLine
import de.webis.webisstud.thesis.reimer.ltr.sampling.RunSampling
import de.webis.webisstud.thesis.reimer.model.Corpus

object RemoveDuplicatesRunUndersampling : RunSampling {

	override val id = "remove-duplicates"

	override fun sample(items: List<JudgedRunLine>, corpus: Corpus): List<JudgedRunLine> {
		val groups = corpus.fingerprintGroups.groups
		return items.groupBy { it.topicId }
				.values
				.map { sampleTopic(it, groups) }
				.flatten()
	}

	private fun sampleTopic(items: List<JudgedRunLine>, groups: Set<FingerprintGroup>): List<JudgedRunLine> {
		return items
				.groupBy { runLine ->
					groups.find { runLine.documentId in it.ids }?.hash
				}
				.map { (groupHash, groupRun) ->
					when (groupHash) {
						null -> groupRun // Leave documents without duplicates untouched.
						else -> groupRun.take(1)
					}
				}
				.flatten()
				.mapIndexed { index, runLine ->
					// Fix run line positions.
					runLine.copy(runLine = runLine.runLine.copy(position = index + 1))
				}
	}
}