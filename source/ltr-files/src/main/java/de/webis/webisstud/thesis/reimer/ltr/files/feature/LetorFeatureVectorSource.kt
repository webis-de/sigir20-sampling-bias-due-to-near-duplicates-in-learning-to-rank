package de.webis.webisstud.thesis.reimer.ltr.files.feature

import de.webis.webisstud.thesis.reimer.letor.hasLetorQuerySet
import de.webis.webisstud.thesis.reimer.letor.letorQuerySet
import de.webis.webisstud.thesis.reimer.model.TrecTask
import de.webis.webisstud.thesis.reimer.model.format.FeatureVectorLineFormat

class LetorFeatureVectorSource(
        private val task: TrecTask
) : FeatureVectorSource() {

    init {
        require(task.hasLetorQuerySet) { "No searchable index found for corpus." }
    }

    private val elements by lazy {
        val vectorsFile = task
                .letorQuerySet
                .supervised
                .normVectors
        println("Load features <- ${vectorsFile.path}") // TODO
        vectorsFile
                .useLines { lines ->
                    lines.map(FeatureVectorLineFormat::parse).toList()
                }
    }

    override fun iterator() = elements.iterator()
}
