package de.webis.webisstud.thesis.reimer

import de.webis.webisstud.thesis.reimer.data.files
import de.webis.webisstud.thesis.reimer.groups.FingerprintGroup
import de.webis.webisstud.thesis.reimer.groups.fingerprintGroups
import de.webis.webisstud.thesis.reimer.model.JudgedTopic
import de.webis.webisstud.thesis.reimer.model.TrecTask
import de.webis.webisstud.thesis.reimer.model.format.RunLineFormat
import dev.reimer.kotlin.jvm.ktx.prepend
import dev.reimer.kotlin.jvm.ktx.writeLinesTo
import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.SwingWrapper
import org.knowm.xchart.XYChart
import org.knowm.xchart.style.MatlabTheme
import org.knowm.xchart.style.Theme
import org.knowm.xchart.style.colors.MatlabSeriesColors
import java.awt.Color
import kotlin.math.pow
import kotlin.math.roundToInt

fun main() {
    TrecTask.Web2012.topics.forEach(JudgedTopic<*>::saveIncrementalLoss)
//    TrecTask.Web2012.getTopic("194").displayIncrementalLossPlots()
//    TrecTask.Web2012.getTopic("167").displayIncrementalLossPlots()
}

private fun JudgedTopic<*>.measureIncrementalLoss(group: FingerprintGroup): List<LossMetadata> {
    val representative = get(group.first())
    if (representative == null) {
        println("Representative document ${group.first()} not found.")
        return emptyList()
    }
    return (1..group.size)
            .mapNotNull { i ->
                val duplicateCount = i - 1
                val windowFiles = if (i != group.size) {
                    files.tag("Group ${group.hash}").tag("Take $i")
                } else {
                    files
                }
                val runs = windowFiles.rerankedRuns.useLines { lines ->
                    lines.map(RunLineFormat::parse).toList()
                }

                val representativeRun = runs.find { it.documentId == representative.id }
                if (representativeRun == null) {
                    println("Representative document ${representative.id} not found.")
                    return@mapNotNull null
                }

                // Quadratic error
                val pointwiseLoss = (representativeRun.score - representative.judgement).pow(2)

                // 0-1 loss
                val pairwiseLoss = runs.map { run ->
                    val deltaRelevance = getDocument(run.documentId).judgement - representative.judgement
                    val deltaScore = run.score - representativeRun.score
                    if (deltaRelevance * deltaScore < 0) 1f else 0f
                }.sum() / runs.size

                val listwiseLoss = 0f // TODO

                val loss = Loss(pointwiseLoss, pairwiseLoss, listwiseLoss)
                val metadata = LossMetadata(
                        id,
                        representative.id,
                        duplicateCount,
                        representative.judgement,
                        runs.indexOfFirst { it.documentId == representative.id },
                        loss
                )
                println(metadata.toCsvLine())

                metadata
            }
}

private fun JudgedTopic<*>.measureIncrementalLoss(): Sequence<LossMetadata> {
    val groups = task.corpus.fingerprintGroups
    return groups.filter(this)
            .groups
            .asSequence()
            .flatMap { group ->
                measureIncrementalLoss(group).asSequence()
            }
}

fun JudgedTopic<*>.saveIncrementalLoss() {
    measureIncrementalLoss()
            .map { it.toCsvLine() }
            .prepend(LossMetadata.CSV_HEADER)
            .writeLinesTo(files.rankingLoss)
}

object ChartTheme : Theme by MatlabTheme() {

    private val colors = MatlabSeriesColors()
            .seriesColors
            .map { it.fadeOut(0.5) }
            .toTypedArray()

    private fun Color.fadeOut(amount: Double) = Color(red, green, blue, (alpha * (1 - amount)).roundToInt())

    override fun getSeriesColors() = colors
}

private fun xyChartOf(configuration: XYChart.() -> Unit) =
        XYChart(400, 400, ChartTheme).apply(configuration)

val JudgedTopic<*>.incrementalLossPlots: List<XYChart>
    get() {
        val pointwisePlot = xyChartOf {
            title = "Pointwise loss"
            xAxisTitle = "Duplicate count"
            yAxisTitle = "Δ pointwise loss"
        }
        val pairwisePlot = xyChartOf {
            title = "Pairwise loss"
            xAxisTitle = "Duplicate count"
            yAxisTitle = "Δ pointwise loss"
        }
        val listwisePlot = xyChartOf {
            title = "Listwise loss"
            xAxisTitle = "Duplicate count"
            yAxisTitle = "Δ pointwise loss"
        }
        val rankPlot = xyChartOf {
            title = "Rank"
            xAxisTitle = "Duplicate count"
            yAxisTitle = "Δ rank"
        }
        val relevantRankPlot = xyChartOf {
            title = "Rank (relevant)"
            xAxisTitle = "Duplicate count"
            yAxisTitle = "Δ rank"
        }
        val irrelevantRankPlot = xyChartOf {
            title = "Rank (irrelevant)"
            xAxisTitle = "Duplicate count"
            yAxisTitle = "Δ rank"
        }

        val groupedLoss = measureIncrementalLoss().groupBy { it.documentId }
        for (id in groupedLoss.keys) {
            val lossMetadata = groupedLoss.getValue(id)
            val duplicateCounts = lossMetadata.map { it.duplicateCount }

            val pointwiseLosses = lossMetadata.map { it.loss.pointwise }
            val pointwiseLossDeltas = pointwiseLosses.map { it - pointwiseLosses.first() }
            pointwisePlot.addSeries(id, duplicateCounts, pointwiseLossDeltas)

            val pairwiseLosses = lossMetadata.map { it.loss.pairwise }
            val pairwiseLossDeltas = pairwiseLosses.map { it - pairwiseLosses.first() }
            pairwisePlot.addSeries(id, duplicateCounts, pairwiseLossDeltas)

            val listwiseLosses = lossMetadata.map { it.loss.listwise }
            val listwiseLossDeltas = listwiseLosses.map { it - listwiseLosses.first() }
            listwisePlot.addSeries(id, duplicateCounts, listwiseLossDeltas)

            val ranks = lossMetadata.map { it.rank }
            val rankDeltas = ranks.map { it - ranks.first() }
            rankPlot.addSeries(id, duplicateCounts, rankDeltas)
            if (lossMetadata.first().judgement > 0) {
                relevantRankPlot.addSeries(id, duplicateCounts, rankDeltas)
                irrelevantRankPlot.addSeries(id, listOf(0), listOf(0))
            } else {
                irrelevantRankPlot.addSeries(id, duplicateCounts, rankDeltas)
                relevantRankPlot.addSeries(id, listOf(0), listOf(0))
            }
        }

        return listOf(pointwisePlot, pairwisePlot, listwisePlot, rankPlot, relevantRankPlot, irrelevantRankPlot)
    }

fun JudgedTopic<*>.displayIncrementalLossPlots() {
    SwingWrapper(incrementalLossPlots)
            .displayChartMatrix("Losses for topic $id, ${task.title}")
}

fun JudgedTopic<*>.saveIncrementalLossPlots() {
    BitmapEncoder.saveBitmap(
            incrementalLossPlots,
            2,
            3,
            files.rankingLossPlots.outputStream(),
            BitmapEncoder.BitmapFormat.PNG
    )
}

private data class LossMetadata(
        val topicId: String,
        val documentId: String,
        val duplicateCount: Int,
        val judgement: Int,
        val rank: Int,
        val loss: Loss
) {
    companion object {
        const val CSV_HEADER = "Topic ID,Document ID,Duplicate count,Judgement,Rank," +
                "Pointwise loss,Pairwise loss,Listwise loss"
    }

    fun toCsvLine() = "$topicId,$documentId,$duplicateCount,$judgement,$rank," +
            "${loss.pointwise},${loss.pairwise},${loss.listwise}"
}