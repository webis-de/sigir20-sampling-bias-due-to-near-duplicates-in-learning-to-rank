package de.webis.webisstud.thesis.reimer

import de.webis.webisstud.thesis.reimer.data.dataDir
import de.webis.webisstud.thesis.reimer.groups.canonical.CanonicalFingerprintGroups
import de.webis.webisstud.thesis.reimer.groups.canonical.canonicalFingerprintGroups
import de.webis.webisstud.thesis.reimer.ltr.split.FeatureVectorSplitter
import de.webis.webisstud.thesis.reimer.ltr.split.Split
import de.webis.webisstud.thesis.reimer.ltr.split.featureVectorSplitters
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.JudgedDocument
import de.webis.webisstud.thesis.reimer.model.JudgedTopic
import de.webis.webisstud.thesis.reimer.model.trecTopics
import dev.reimer.kotlin.jvm.ktx.append
import dev.reimer.kotlin.jvm.ktx.prepareNewFile
import dev.reimer.kotlin.jvm.ktx.prepend
import dev.reimer.kotlin.jvm.ktx.writeLinesTo
import kotlin.math.roundToInt

fun main(args: Array<String>) {
    val corpus = Corpus.valueOfCaseInsensitive(args[0])
    val splits = corpus.featureVectorSplitters
    val topics = corpus.trecTopics.toSet()
    val groups = corpus.canonicalFingerprintGroups
    splits.asSequence()
            .map {
                it to it.printCountRedundancyRelevance(topics, groups)
            }
            .map { (split, stats) ->
                val (trainingStats, testStats) = stats
                """    ${split.id} & ${trainingStats.size} & ${trainingStats.redundant} & ${(trainingStats.relevanceDegree * 100).roundToInt()}\,\% & ${testStats.size} & ${testStats.redundant} & ${(testStats.relevanceDegree * 100).roundToInt()}\,\% \\"""
            }
            .prepend(
                    """\begin{tabular}{@{}l@{\quad}rrr@{\quad}rrr@{}}""",
                    """    \toprule""",
                    """    & \multicolumn{3}{@{}c@{}}{\textbf{Training Labels}} & \multicolumn{3}{@{}c@{}}{\textbf{Test Labels}} \\""",
                    """    \cmidrule(r{1em}){2-4} \cmidrule{5-7}""",
                    """    & Count & Red.\ & Rel.\ & Count & Red.\ & Rel.\ \\""",
                    """    \midrule"""
            )
            .append(
                    """    \bottomrule""",
                    """\end{tabular}"""
            )
            .writeLinesTo(corpus.dataDir.resolve("train-test-splits.tex").apply { prepareNewFile() })
}

private fun FeatureVectorSplitter.printCountRedundancyRelevance(allTopics: Set<JudgedTopic<JudgedDocument>>, groups: CanonicalFingerprintGroups): Pair<SplitStats, SplitStats> {
    println("Split: $id")

    val metadata = allTopics.flatMap { it.documents }
    val trainingDocuments = metadata.filter { getSplit(it.asMetadata()) == Split.Training }
    val testDocuments = metadata.filter { getSplit(it.asMetadata()) == Split.Test }

    print("Training: ")
    val trainingStats = trainingDocuments.printCountRedundancyRelevance(groups)
    print("Test: ")
    val testStats = testDocuments.printCountRedundancyRelevance(groups)
    println()

    return trainingStats to testStats
}

private fun List<JudgedDocument>.printCountRedundancyRelevance(groups: CanonicalFingerprintGroups): SplitStats {
    val count = size
    print("count=$count, ")
    val redundantIds: Set<String> = groups.groups.flatMapTo(mutableSetOf()) { it.ids }
    val redundant = filter { it.id in redundantIds }
    val redundancy = redundant.size
    val redundancyDegree = redundancy.toDouble() / count
    print("redundancy=${redundancy} (${(redundancyDegree * 100).roundToInt()}%), ")
    val relevance = redundant.filter { it.isRelevant }.size.toFloat() / redundant.size
    print("redundant-relevance=${(relevance * 100).roundToInt()}%")
    println()

    return SplitStats(count, redundancy, relevance)
}

data class SplitStats(val size: Int, val redundant: Int, val relevanceDegree: Float)
