package de.webis.webisstud.thesis.reimer.evaluation.sampling

import de.webis.webisstud.thesis.reimer.groups.FingerprintGroup
import de.webis.webisstud.thesis.reimer.groups.fingerprintGroups
import de.webis.webisstud.thesis.reimer.ltr.JudgedRunLine
import de.webis.webisstud.thesis.reimer.ltr.sampling.RunSampling
import de.webis.webisstud.thesis.reimer.model.Corpus

object DuplicatesIrrelevantRunSampling : RunSampling {

    override val id = "duplicates-irrelevant"

    override fun sample(items: List<JudgedRunLine>, corpus: Corpus): List<JudgedRunLine> {
        val groups = corpus.fingerprintGroups.groups
        return items.groupBy { it.topicId }
            .values
            .map { sampleTopic(it, groups) }
            .flatten()
    }

    private fun sampleTopic(items: List<JudgedRunLine>, groups: Set<FingerprintGroup>): List<JudgedRunLine> {
        val includedGroups = mutableSetOf<Long>()
        return items
            .map { runLine ->
                when (val group = groups.find { runLine.documentId in it.ids }?.hash) {
                    null -> runLine
                    in includedGroups -> runLine.copy(relevance = runLine.relevance.copy(judgement = 0))
                    else -> {
                        includedGroups += group
                        runLine
                    }
                }
            }
    }
}