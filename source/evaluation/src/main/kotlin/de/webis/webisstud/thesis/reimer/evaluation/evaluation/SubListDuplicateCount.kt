package de.webis.webisstud.thesis.reimer.evaluation.evaluation

import de.webis.webisstud.thesis.reimer.evaluation.RankingEvaluation
import de.webis.webisstud.thesis.reimer.groups.fingerprintGroups
import de.webis.webisstud.thesis.reimer.ltr.JudgedRunLine
import de.webis.webisstud.thesis.reimer.model.Corpus
import kotlinx.serialization.map
import kotlinx.serialization.serializer

/**
 * For each topic, calculate a map containing a list of duplicate counts for the first n runs.
 */
object SubListDuplicateCount : RankingEvaluation<Map<Int, Int>> {

	override val serializer = (Int.serializer() to Int.serializer()).map

	override val id = "sub-list-duplicate-count"

	override fun evaluate(run: List<JudgedRunLine>, corpus: Corpus): Map<Int, Int> {
		val groups = corpus.fingerprintGroups
		val size = run.size
		return run.asReversed()
				.asSequence()
				.map {
					groups.anyGroupContains(it.documentId)
				}
				.windowed(run.size, partialWindows = true) // FIXME
				.mapIndexed { index, firstNRuns ->
					val n = size - index
					n to firstNRuns.count { it }
				}
				.toMap()
    }
}
