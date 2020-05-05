package de.webis.webisstud.thesis.reimer.ltr.files

import de.webis.webisstud.thesis.reimer.data.dataDir
import de.webis.webisstud.thesis.reimer.groups.fingerprintGroups
import de.webis.webisstud.thesis.reimer.ltr.files.source.*
import de.webis.webisstud.thesis.reimer.model.TrecTask

fun TrecTask.buildLtrFiles() {
    val qrelSource = qrelSource
    val runSource = runSource
    val featureSource = featureVectorSource

    for (topic in topics) {
        println("Generating LTR files for topic $id ($size documents, query: '$title').")
        if (isEmpty()) return

        val topicDir = topic.dataDir
        qrelSource.filter { it.topicId == topic.id }.cached(topicDir.resolve("qrels.txt")).cacheNow()
        runSource.filter { it.topicId == topic.id }.cached(topicDir.resolve("run-file.txt")).cacheNow()
        val topicFeatureSource =
                featureSource.filter { it.topicId == topic.id }.cached(topicDir.resolve("features.fv")).cacheNow()

        val groups = corpus.fingerprintGroups
        topicFeatureSource.getDuplicatesRemoved(groups).cacheNow()
        topicFeatureSource.getSuccessiveDuplicates(groups).values.forEach { map ->
            map.values.forEach { source ->
                source.cacheNow()
            }
        }
    }
}
