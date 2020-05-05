package de.webis.webisstud.thesis.reimer.ltr.files.source

import de.webis.webisstud.thesis.reimer.data.dataDir
import de.webis.webisstud.thesis.reimer.groups.FingerprintGroups
import de.webis.webisstud.thesis.reimer.letor.hasLetorQuerySet
import de.webis.webisstud.thesis.reimer.ltr.files.feature.ClueWeb09MergedFeatureVectorSource
import de.webis.webisstud.thesis.reimer.ltr.files.feature.LetorFeatureVectorSource
import de.webis.webisstud.thesis.reimer.ltr.files.qrel.QrelSource
import de.webis.webisstud.thesis.reimer.ltr.files.run.RunSource
import de.webis.webisstud.thesis.reimer.model.*
import de.webis.webisstud.thesis.reimer.model.format.FeatureVectorLineFormat
import de.webis.webisstud.thesis.reimer.model.format.QrelLineFormat
import de.webis.webisstud.thesis.reimer.model.format.RunLineFormat
import dev.reimer.kotlin.jvm.ktx.mapToMap

val TrecTask.featureVectorSource: CachedMetadataSource<FeatureVector>
    get() {
        return when {
            hasLetorQuerySet -> LetorFeatureVectorSource(this)
            else -> when (corpus) {
                Corpus.ClueWeb09 -> ClueWeb09MergedFeatureVectorSource(this)
                else -> emptySequence<FeatureVector>().toMetadataSource(FeatureVectorLineFormat)
            }
        }.cached(dataDir.resolve("features.fv"))
    }

val TrecTask.runSource
    get() = RunSource(featureVectorSource).cached(dataDir.resolve("runs.txt"))

val TrecTask.qrelSource
    get() = QrelSource(this).cached(dataDir.resolve("qrels.txt"))

val Corpus.featureVectorSource: CachedMetadataSource<FeatureVector>
    get() {
        return trecTasks
                .flatMap(TrecTask::featureVectorSource)
                .toMetadataSource(FeatureVectorLineFormat)
                .cached(dataDir.resolve("features.fv"))
    }

val Corpus.runSource: CachedMetadataSource<RunLine>
    get() {
        return trecTasks
                .flatMap(TrecTask::runSource)
                .toMetadataSource(RunLineFormat)
                .cached(dataDir.resolve("runs.txt"))
    }

val Corpus.qrelSource: CachedMetadataSource<Relevance>
    get() {
        return trecTasks
                .flatMap(TrecTask::qrelSource)
                .toMetadataSource(QrelLineFormat)
                .cached(dataDir.resolve("qrels.txt"))
    }

fun CachedMetadataSource<FeatureVector>.getDuplicatesRemoved(groups: FingerprintGroups): CachedMetadataSource<FeatureVector> {
    val duplicatesRemovedCache = cache.resolveSibling("duplicate-removed").resolve("features.fv")
    return filter { vector ->
        val documentId = vector.documentId
        val group = groups.values.find {
            documentId in it
        }
        group == null || group.first() == documentId
    }.cached(duplicatesRemovedCache)
}

fun CachedMetadataSource<FeatureVector>.getSuccessiveDuplicates(groups: FingerprintGroups): Map<Long, Map<Int, CachedMetadataSource<FeatureVector>>> {
    val allIds = map { it.documentId }.toList()
    val topicGroups = groups.filterIds(allIds)
    return topicGroups.mapToMap { group ->
        val groupHash = group.hash
        val groupDir = cache.resolveSibling("group-${groupHash}")
        groupHash to group.idSubLists.asIterable().mapToMap { window ->
            val windowSize = window.size
            val windowDir = groupDir.resolve("take-${windowSize}")
            windowSize to filter { vector ->
                val documentId = vector.documentId
                // Documents which are not in the group or in the current group window.
                documentId !in group || documentId in window
            }.cached(windowDir.resolve("features.fv")).cacheNow()
        }
    }
}