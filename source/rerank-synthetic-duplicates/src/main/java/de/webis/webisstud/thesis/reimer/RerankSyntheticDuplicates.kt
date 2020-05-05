package de.webis.webisstud.thesis.reimer

import de.webis.webisstud.thesis.reimer.data.dataDir
import de.webis.webisstud.thesis.reimer.ltr.MetricType
import de.webis.webisstud.thesis.reimer.ltr.RankerType
import de.webis.webisstud.thesis.reimer.ltr.files.source.*
import de.webis.webisstud.thesis.reimer.ltr.trainRerankTaskRuns
import de.webis.webisstud.thesis.reimer.model.FeatureVector
import de.webis.webisstud.thesis.reimer.model.TrecTask
import java.io.File
import kotlin.math.sign
import kotlin.random.Random

fun TrecTask.rerankSyntheticDuplicates() {
    val trainingDir = dataDir
            .resolve("training")
            .apply { mkdirs() }

    fun first(dir: File, comparator: Comparator<FeatureVector>, n: Int = 100) {
        val sortedVectors = featureVectorSource.sortedWith(comparator)

        for (i in 0..n) {
            sortedVectors
                    .drop(i)
                    .cached(dir.resolve("train-$i.fv"))
                    .trainRerankTaskRuns(this)
                    .cached(dir.resolve("runs-$i.txt"))
                    .cacheNow()
        }
    }

    fun worstRelevant(n: Int = 100) {
        val dir = trainingDir
                .resolve("worst-relevant")
                .apply { mkdirs() }
        // Lowest positive relevance labels should come first.
        val worstRelevantComparator =
                compareBy<FeatureVector> { -it.relevance.sign }.thenBy { it.relevance }
        first(dir, worstRelevantComparator, n)
    }

    fun bestIrrelevant(n: Int = 100) {
        val dir = trainingDir
                .resolve("best-irrelevant")
                .apply { mkdirs() }
        // Lowest positive relevance labels should come first.
        val bestIrrelevantComparator =
                compareBy<FeatureVector> { it.relevance.sign }.thenBy { -it.relevance }
        first(dir, bestIrrelevantComparator, n)
    }

    fun balanceRelevantIrrelevant(seed: Int = 0) {
        val random = Random(seed)

        val dir = trainingDir
                .resolve("balance")
                .apply { mkdirs() }

        var currentRelevantVectors = featureVectorSource.filter { it.relevance > 0 }
        var currentIrrelevantVectors = featureVectorSource.filterNot { it.relevance > 0 }

        val relevantVectors = currentRelevantVectors.toList()
        val irrelevantVectors = currentIrrelevantVectors.toList()

        var i = 0
        while (currentRelevantVectors.toList().size != currentIrrelevantVectors.toList().size) {
            when {
                currentRelevantVectors.toList().size < currentIrrelevantVectors.toList().size -> {
                    currentRelevantVectors += relevantVectors.random(random)
                    currentIrrelevantVectors -= currentIrrelevantVectors.toList().random(random)
                }
                else -> {
                    currentIrrelevantVectors += irrelevantVectors.random(random)
                    currentRelevantVectors -= currentRelevantVectors.toList().random(random)
                }
            }

            (currentRelevantVectors + currentIrrelevantVectors)
                    .cached(dir.resolve("train-$i.fv"))
                    .trainRerankTaskRuns(this)
                    .cached(dir.resolve("runs-$i.txt"))
                    .cacheNow()

            i++
        }
    }

    worstRelevant()
    bestIrrelevant()
    balanceRelevantIrrelevant()
}

fun CachedMetadataSource<FeatureVector>.trainRerankTaskRuns(task: TrecTask) =
        trainRerankTaskRuns(RankerType.RankNet, MetricType.Ndcg(20), task.runSource, task.featureVectorSource)

fun main() = TrecTask.MillionQuery2007.rerankSyntheticDuplicates()
