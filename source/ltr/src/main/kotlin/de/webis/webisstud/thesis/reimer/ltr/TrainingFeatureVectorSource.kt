package de.webis.webisstud.thesis.reimer.ltr

import de.webis.webisstud.thesis.reimer.ltr.files.source.CachedMetadataSource
import de.webis.webisstud.thesis.reimer.ltr.files.source.MetadataSource
import de.webis.webisstud.thesis.reimer.ltr.pipeline.Reranker
import de.webis.webisstud.thesis.reimer.ltr.pipeline.TrainedReranker
import de.webis.webisstud.thesis.reimer.model.FeatureVector

class TrainingFeatureVectorSource(
        private val source: CachedMetadataSource<FeatureVector>,
        private val rankerType: RankerType,
        private val metricType: MetricType
) : MetadataSource<FeatureVector> {

    override val format = source.format

    val reranker: Reranker
        get() {
            source.cacheNow()
            return TrainedReranker(source.cache, rankerType, metricType)
        }

    override fun iterator() = source.iterator()
}
