package de.webis.webisstud.thesis.reimer.ltr.files

import de.webis.webisstud.thesis.reimer.ltr.files.source.CachedMetadataSource
import de.webis.webisstud.thesis.reimer.model.Relevance

interface HasRelevances {
    val relevances: CachedMetadataSource<Relevance>
}