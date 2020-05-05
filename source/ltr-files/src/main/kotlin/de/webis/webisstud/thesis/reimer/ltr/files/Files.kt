package de.webis.webisstud.thesis.reimer.ltr.files

import de.webis.webisstud.thesis.reimer.ltr.files.source.CachedMetadataSource
import de.webis.webisstud.thesis.reimer.ltr.files.source.featureVectorSource
import de.webis.webisstud.thesis.reimer.ltr.files.source.qrelSource
import de.webis.webisstud.thesis.reimer.ltr.files.source.runSource
import de.webis.webisstud.thesis.reimer.model.*

fun main(args: Array<String>) {
    val corpus = Corpus.find(args[0])
    val featureVectorSource: CachedMetadataSource<FeatureVector>
	val runSource: CachedMetadataSource<RunLine>
	val qrelSource: CachedMetadataSource<Relevance>
    if (corpus != null) {
        featureVectorSource = corpus.featureVectorSource
        runSource = corpus.runSource
        qrelSource = corpus.qrelSource
    } else {
        val task = TrecTask.valueOfCaseInsensitive(args[0])
        featureVectorSource = task.featureVectorSource
        runSource = task.runSource
        qrelSource = task.qrelSource
    }

    qrelSource.cacheNow()
    runSource.cacheNow()
    featureVectorSource.cacheNow()
}