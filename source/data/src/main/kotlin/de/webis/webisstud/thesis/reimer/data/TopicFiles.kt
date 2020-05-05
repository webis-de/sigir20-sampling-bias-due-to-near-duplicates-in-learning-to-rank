package de.webis.webisstud.thesis.reimer.data

import de.webis.webisstud.thesis.reimer.model.Document
import de.webis.webisstud.thesis.reimer.model.Topic
import java.io.File

class TopicFiles<T : Document> private constructor(
        private val dataDir: File,
        val tags: Set<String> = emptySet()
) {

    private companion object {
        private val ILLEGAL_TAG_CHARACTER_REGEX = Regex(" ")
    }

    constructor(topic: Topic<T>) : this(topic.dataDir)

    constructor(topic: Iterable<Topic<T>>) : this(topic.dataDir)

    private val encodedTags
        get() = tags
                .asSequence()
                .map { it.replace(ILLEGAL_TAG_CHARACTER_REGEX, "-") }
                .map(String::toLowerCase)
                .filter(String::isNotBlank)

    private fun File.resolveTags() = encodedTags.fold(this) { file, tag -> file.resolve(tag) }

    private val featureVectorsName = "features.fv"
    val featureVectors get() = dataDir.resolveTags().resolve(featureVectorsName)

    private val qrelsName = "qrels.txt"
    val qrels get() = dataDir.resolve(qrelsName)

    private val runsName = "run-file.txt"
    val runs get() = dataDir.resolve(runsName)

    private val rerankedRunsName = "run-file-reranked.txt"
    val rerankedRuns get() = dataDir.resolveTags().resolve(rerankedRunsName)

    private val rankCorrelationsName get() = "rank-correlations.json"
    val rankCorrelations get() = dataDir.resolve(rankCorrelationsName)

    private val rankingLossName get() = "ranking-loss.csv"
    val rankingLoss get() = dataDir.resolve(rankingLossName)

    private val rankingLossChartsName get() = "ranking-loss.png"
    val rankingLossPlots get() = dataDir.resolve(rankingLossChartsName)

    fun tag(tags: Iterable<String>) = TopicFiles<T>(dataDir, this.tags + tags)
    fun tag(vararg tags: String) = tag(tags.asIterable())
    fun tag(tag: String) = tag(setOf(tag))
    fun withoutTags() = TopicFiles<T>(dataDir)
}