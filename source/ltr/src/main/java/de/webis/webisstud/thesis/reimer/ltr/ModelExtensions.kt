package de.webis.webisstud.thesis.reimer.ltr

import de.webis.webisstud.thesis.reimer.ltr.files.source.CachedMetadataSource
import de.webis.webisstud.thesis.reimer.ltr.files.source.MetadataSource
import de.webis.webisstud.thesis.reimer.model.FeatureVector
import de.webis.webisstud.thesis.reimer.model.RunLine

fun CachedMetadataSource<FeatureVector>.trainRerankTaskRuns(
		rankerType: RankerType,
		metricType: MetricType,
		runSource: MetadataSource<RunLine>,
		featureVectorSource: MetadataSource<FeatureVector>
): MetadataSource<RunLine> =
		RerankedRunSource(trained(rankerType, metricType), runSource, featureVectorSource)

fun CachedMetadataSource<FeatureVector>.trained(
        rankerType: RankerType,
        metricType: MetricType
) = TrainingFeatureVectorSource(this, rankerType, metricType)

fun TrainingFeatureVectorSource.rerank(
		runSource: MetadataSource<RunLine>,
		featureVectorSource: MetadataSource<FeatureVector>
): MetadataSource<RunLine> =
		RerankedRunSource(this, runSource, featureVectorSource)
