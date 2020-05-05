package de.webis.webisstud.thesis.reimer.ltr.files.source

import dev.reimer.kotlin.jvm.ktx.prepareNewFile
import dev.reimer.kotlin.jvm.ktx.writeLinesTo
import java.io.File

class CachedMetadataSource<T>(
        private val source: MetadataSource<T>,
        val cache: File
) : MetadataSource<T> {

    override val format get() = source.format

    // TODO: What about parallelization? We should synchronize file write access.
    private val elements by lazy {
        if (!cache.exists() || !cache.isFile || cache.length() == 0L) {
            source.asSequence()
                    .map(format::format)
                    .writeLinesTo(cache.apply(File::prepareNewFile))
            println("Save cache -> ${cache.path}") // TODO
        }
        println("Load cache <- ${cache.path}") // TODO
        cache.useLines { lines ->
            lines.map(format::parse).toList()
        }.toList()
    }

    fun cacheNow(): CachedMetadataSource<T> {
        iterator()
        return this
    }

    override fun iterator() = elements.iterator()
}
