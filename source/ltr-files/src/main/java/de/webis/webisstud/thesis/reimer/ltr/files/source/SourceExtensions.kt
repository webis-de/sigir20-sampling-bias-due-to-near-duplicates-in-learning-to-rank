package de.webis.webisstud.thesis.reimer.ltr.files.source

import de.webis.webisstud.thesis.reimer.model.format.StringFormat
import java.io.File

fun <T> Sequence<T>.toMetadataSource(format: StringFormat<T>): MetadataSource<T> {
    return object : MetadataSource<T>, Sequence<T> by this {
        override val format: StringFormat<T> = format
    }
}

fun <T> MetadataSource<T>.cached(cacheFile: File) =
        CachedMetadataSource(this, cacheFile)

fun <T> MetadataSource<T>.writeFeaturesTo(file: File) =
        cached(file).cacheNow()

fun <T> MetadataSource<T>.transform(transform: Sequence<T>.() -> Sequence<T>): MetadataSource<T> =
        TransformMetadataSource(this, transform)

fun <T> MetadataSource<T>.filter(predicate: (T) -> Boolean) =
        transform { filter(predicate) }

fun <T> MetadataSource<T>.filterNot(predicate: (T) -> Boolean) =
        transform { filterNot(predicate) }

fun <T> MetadataSource<T>.sortedWith(comparator: Comparator<T>) =
        transform { sortedWith(comparator) }

fun <T> MetadataSource<T>.drop(n: Int) =
        transform { drop(n) }

operator fun <T> MetadataSource<T>.plus(element: T) =
        transform { this + element }

operator fun <T> MetadataSource<T>.plus(elements: Iterable<T>) =
        transform { this + elements }

operator fun <T> MetadataSource<T>.plus(elements: Sequence<T>) =
        transform { this + elements }

operator fun <T> MetadataSource<T>.minus(element: T) =
        transform { this - element }
