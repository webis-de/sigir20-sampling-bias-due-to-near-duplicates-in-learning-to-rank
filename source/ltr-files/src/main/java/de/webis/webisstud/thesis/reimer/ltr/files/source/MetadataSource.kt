package de.webis.webisstud.thesis.reimer.ltr.files.source

import de.webis.webisstud.thesis.reimer.model.format.StringFormat

interface MetadataSource<T> : Sequence<T> {
    val format: StringFormat<T>
}