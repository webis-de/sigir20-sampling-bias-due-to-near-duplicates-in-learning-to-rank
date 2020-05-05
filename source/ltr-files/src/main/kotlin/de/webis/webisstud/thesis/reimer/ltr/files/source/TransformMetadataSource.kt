package de.webis.webisstud.thesis.reimer.ltr.files.source

class TransformMetadataSource<T>(
        private val source: MetadataSource<T>,
        private val transform: Sequence<T>.() -> Sequence<T>
) : MetadataSource<T> {

    override val format get() = source.format

    private val elements by lazy {
        source.transform().toList()
    }

    override fun iterator() = elements.iterator()
}
