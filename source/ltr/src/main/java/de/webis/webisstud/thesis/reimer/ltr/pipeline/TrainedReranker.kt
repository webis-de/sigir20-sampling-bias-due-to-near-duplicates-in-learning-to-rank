package de.webis.webisstud.thesis.reimer.ltr.pipeline

import ciir.umass.edu.features.FeatureManager
import ciir.umass.edu.learning.RankList
import ciir.umass.edu.learning.tree.LambdaMART
import ciir.umass.edu.utilities.MyThreadPool
import de.webis.webisstud.thesis.reimer.ltr.MetricType
import de.webis.webisstud.thesis.reimer.ltr.RankerType
import de.webis.webisstud.thesis.reimer.ltr.util.SimpleConsoleHandler
import de.webis.webisstud.thesis.reimer.ltr.util.setHandler
import java.io.File
import java.util.logging.LogManager

class TrainedReranker(
        trainFile: File,
        rankerType: RankerType,
        metricType: MetricType
) : RankLibReranker() {

    override val ranker by lazy {

        // Remove bloated log output.
        LogManager.getLogManager().setHandler(SimpleConsoleHandler)

        val trainSet: List<RankList> = FeatureManager.readInput(trainFile.path)
        val featureDef: IntArray = FeatureManager.getFeatureFromSampleVector(trainSet)

        rankerType.ranker.apply {
            if (this is LambdaMART) {
                // Fix a bug, where the internal thread pool used by LambdaMART
                // would starve, when multiple LambdaMART rankers are launched concurrently.
                MyThreadPool.init(1)
            }

            setTrainingSet(trainSet)
            features = featureDef
            setMetricScorer(metricType.scorer)

            init()
            learn()
        }
    }

    /**
     * Train the ranker immediately.
     *
     * Normally the ranker will automatically be trained at first usage,
     * and consequentially, calling this method should be avoided in gerneral.
     */
    fun train() {
        ranker
    }
}