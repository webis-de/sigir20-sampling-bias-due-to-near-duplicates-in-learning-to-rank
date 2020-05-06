package de.webis.webisstud.thesis.reimer.ltr.files.source

import de.webis.webisstud.thesis.reimer.letor.hasLetorQuerySet
import de.webis.webisstud.thesis.reimer.ltr.files.feature.LetorCorpusFeatureVectorSource
import de.webis.webisstud.thesis.reimer.ltr.files.feature.LetorTaskFeatureVectorSource
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.FeatureVector
import de.webis.webisstud.thesis.reimer.model.TrecTask
import de.webis.webisstud.thesis.reimer.model.format.FeatureVectorLineFormat
import de.webis.webisstud.thesis.reimer.model.trecTasks

private val TrecTask.featureVectorSource: MetadataSource<FeatureVector>
    get() {
        return when {
            hasLetorQuerySet -> LetorTaskFeatureVectorSource(this)
            else -> emptySequence<FeatureVector>().toMetadataSource(FeatureVectorLineFormat)
        }
    }

val Corpus.featureVectorSource: MetadataSource<FeatureVector>
    get() {
        return when {
            hasLetorQuerySet -> LetorCorpusFeatureVectorSource(this)
            else -> {
                trecTasks
                    .flatMap(TrecTask::featureVectorSource)
                    .toMetadataSource(FeatureVectorLineFormat)
            }
        }
    }
