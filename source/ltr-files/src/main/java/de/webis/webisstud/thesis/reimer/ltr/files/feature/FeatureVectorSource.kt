package de.webis.webisstud.thesis.reimer.ltr.files.feature

import de.webis.webisstud.thesis.reimer.ltr.files.source.MetadataSource
import de.webis.webisstud.thesis.reimer.model.FeatureVector
import de.webis.webisstud.thesis.reimer.model.format.FeatureVectorLineFormat

abstract class FeatureVectorSource : MetadataSource<FeatureVector> {
    final override val format = FeatureVectorLineFormat
}
