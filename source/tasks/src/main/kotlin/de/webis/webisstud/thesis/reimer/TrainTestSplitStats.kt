package de.webis.webisstud.thesis.reimer

import de.webis.webisstud.thesis.reimer.data.dataDir
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
    val isCanonical = corpus.canonicalFingerprintGroups
        .flatMap { (_, group) ->
            val globalCanonical = group.globalCanonical
            group.ids.map { it to (it == globalCanonical) }
        }
        .toMap()
    splits.asSequence()
        .map {
            it to it.countRedundancyRelevance(topics, isCanonical)
        }
        .map { (split, stats) ->
            val (trainingStats, testStats) = stats
            """    ${split.id} & ${trainingStats.size} & ${(trainingStats.relevanceDegree * 100).roundToInt()}\,\% & ${trainingStats.redundantSize} & ${trainingStats.canonicalSize} & ${testStats.size} & ${(testStats.relevanceDegree * 100).roundToInt()}\,\% & ${testStats.redundantSize} & ${testStats.canonicalSize} \\"""
        }
        .prepend(
            """\begin{tabular}{l@{\quad}rrrrr@{\quad}rrrrr}""",
            """    \toprule""",
            """    & \multicolumn{4}{c@{\quad}}{\textbf{Training Labels}} & \multicolumn{4}{c}{\textbf{Test Labels}} \\""",
            """    \cmidrule(r{1em}){2-5} \cmidrule{6-9}""",
            """    & Count & \%\,Rel. & Red. & Canon. & Count & \%\,Rel. & Red. & Canon. \\""",
            """    \midrule"""
        )
        .append(
            """    \bottomrule""",
            """\end{tabular}"""
        )
        .writeLinesTo(corpus.dataDir.resolve("train-test-splits.tex").apply { prepareNewFile() })
}

private fun FeatureVectorSplitter.countRedundancyRelevance(
    allTopics: Set<JudgedTopic<JudgedDocument>>,
    isCanonical: Map<String, Boolean>
): Pair<SplitStats, SplitStats> {
    println("Split: $id")

    val metadata = allTopics.flatMap { it.documents }
    val trainingDocuments = metadata.filter { getSplit(it.asMetadata()) == Split.Training }
    val testDocuments = metadata.filter { getSplit(it.asMetadata()) == Split.Test }

    print("Training: ")
    val trainingStats = trainingDocuments.countRedundancyRelevance(isCanonical)
    print("Test: ")
    val testStats = testDocuments.countRedundancyRelevance(isCanonical)
    println()

    return trainingStats to testStats
}

private fun List<JudgedDocument>.countRedundancyRelevance(isCanonical: Map<String, Boolean>): SplitStats {
    val relevant = filter { it.isRelevant }
    val redundant = filter { isCanonical.containsKey(it.id) }
    val redundantRelevant = redundant intersect relevant
    val canonical = redundant.filter { isCanonical.getValue(it.id) }
    val canonicalRelevant = canonical intersect redundantRelevant
    return SplitStats(
        size,
        relevant.size.toFloat() / size,
        redundant.size,
        redundantRelevant.size.toFloat() / redundant.size,
        canonical.size,
        canonicalRelevant.size.toFloat() / canonical.size
    )
}

private data class SplitStats(
    val size: Int,
    val relevanceDegree: Float,
    val redundantSize: Int,
    val redundantRelevanceDegree: Float,
    val canonicalSize: Int,
    val canonicalRelevanceDegree: Float
)
