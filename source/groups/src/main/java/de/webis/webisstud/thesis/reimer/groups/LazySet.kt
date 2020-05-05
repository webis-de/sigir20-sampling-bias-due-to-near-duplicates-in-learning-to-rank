package de.webis.webisstud.thesis.reimer.groups

class LazySet<T>(
        initializer: () -> Set<T>
) : AbstractSet<T>() {

    private val delegate by lazy(initializer)

    override val size: Int
        get() = delegate.size

    override fun iterator(): Iterator<T> = delegate.iterator()
}