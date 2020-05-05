package de.webis.webisstud.thesis.reimer.ltr.files

import de.webis.webisstud.thesis.reimer.ltr.files.source.CachedMetadataSource
import de.webis.webisstud.thesis.reimer.model.RunLine

interface HasRuns {
	val runs: CachedMetadataSource<RunLine>
}