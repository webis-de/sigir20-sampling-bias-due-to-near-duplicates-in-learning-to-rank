package de.webis.webisstud.thesis.reimer.ltr.pipeline

import ciir.umass.edu.learning.Ranker
import ciir.umass.edu.learning.RankerFactory
import java.io.File

internal class ModelReranker(model: File) : RankLibReranker() {

    companion object {
        private val FACTORY = RankerFactory()
    }

    override val ranker: Ranker = FACTORY.loadRankerFromFile(model.absolutePath)
}