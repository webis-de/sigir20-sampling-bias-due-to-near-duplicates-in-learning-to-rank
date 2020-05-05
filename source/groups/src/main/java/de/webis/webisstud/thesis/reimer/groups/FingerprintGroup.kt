package de.webis.webisstud.thesis.reimer.groups

interface FingerprintGroup : List<String>, Map.Entry<Long, FingerprintGroup>, Comparable<FingerprintGroup> {

    val hash: Long

    /**
     * Sorted set of document IDs.
     */
    val ids: List<String>

    override val key get() = hash

    override val value get() = this

    /**
     * Compare this group's hash.
     */
    override fun compareTo(other: FingerprintGroup) = hash.compareTo(other.hash)

    /**
     * Propagation of increasingly big sub-lists of this group's IDs,
     * except for the list containing all IDs.
     */
    val idSubLists: Sequence<List<String>>
        get() = (1 until size)
                .asSequence()
                .map { n -> take(n) }

}
