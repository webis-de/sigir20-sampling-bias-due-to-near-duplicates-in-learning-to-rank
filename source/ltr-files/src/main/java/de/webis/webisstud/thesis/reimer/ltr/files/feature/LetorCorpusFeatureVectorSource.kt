package de.webis.webisstud.thesis.reimer.ltr.files.feature

import de.webis.webisstud.thesis.reimer.letor.hasLetorQuerySet
import de.webis.webisstud.thesis.reimer.letor.letorQuerySet
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.TrecTask
import de.webis.webisstud.thesis.reimer.model.format.FeatureVectorLineFormat

class LetorCorpusFeatureVectorSource(
        private val corpus: Corpus
) : FeatureVectorSource() {

    init {
        require(corpus.hasLetorQuerySet) { "Corpus ${corpus.name} doesn't have LETOR-like features." }
    }

    private val elements by lazy {
        val vectorsFile = corpus
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
