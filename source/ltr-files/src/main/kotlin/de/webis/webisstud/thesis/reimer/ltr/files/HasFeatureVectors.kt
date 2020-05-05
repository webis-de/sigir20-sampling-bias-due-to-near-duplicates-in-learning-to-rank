package de.webis.webisstud.thesis.reimer.ltr.files

import de.webis.webisstud.thesis.reimer.ltr.files.source.CachedMetadataSource
import de.webis.webisstud.thesis.reimer.model.FeatureVector

interface HasFeatureVectors {
    val featureVectors: CachedMetadataSource<FeatureVector>
}