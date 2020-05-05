package de.webis.webisstud.thesis.reimer.groups

import de.webis.webisstud.thesis.reimer.model.Document
import de.webis.webisstud.thesis.reimer.model.JudgedTopic
import de.webis.webisstud.thesis.reimer.model.Topic

interface FingerprintGroups : Set<FingerprintGroup>, Map<Long, FingerprintGroup> {

    override val size get() = values.size

    override fun isEmpty() = values.isEmpty()

    override fun contains(element: FingerprintGroup) = values.contains(element)

    override fun containsAll(elements: Collection<FingerprintGroup>) = values.containsAll(elements)

    override fun containsKey(key: Long) = values.any { it.hash == key }

    override fun containsValue(value: FingerprintGroup) = values.contains(value)

    override fun get(key: Long): FingerprintGroup? = values.find { it.hash == key }

    /**
     * Fingerprint groups's hashes, sorted.
     */
    override val keys: Set<Long> get() = values.mapTo(sortedSetOf()) { it.hash }

    /**
     * Fingerprint groups, sorted by their hash.
     */
    val groups: Set<FingerprintGroup> get() = values

    /**
     * Fingerprint groups, sorted by their hash.
     */
    override val values: Set<FingerprintGroup>

    /**
     * Fingerprint groups, sorted by their hash.
     */
    override val entries: Set<FingerprintGroup> get() = values

    override fun iterator(): Iterator<FingerprintGroup> = values.iterator()

    @Deprecated("Use anyGroupContains instead.", ReplaceWith("anyGroupContains(id)"))
    operator fun contains(id: String): Boolean = anyGroupContains(id)

    fun anyGroupContains(id: String) = entries.any { id in it }

    /**
     * Return a collection of all fingerprint groups from this and [other].
     */
    operator fun plus(other: FingerprintGroups): FingerprintGroups =
            CompositeFingerprintGroups(this, other)

    fun filter(predicate: (FingerprintGroup) -> Boolean): FingerprintGroups =
            FilteredFingerprintGroups(this, predicate)

    fun filterIds(ids: Iterable<String>) = filter { group -> group.any { it in ids } }

    fun filter(documents: Iterable<Document>) = filterIds(documents.map { it.id })

    fun filter(topic: Topic<*>) = filter(topic.documents)

    fun isNotDuplicate(documentId: String): Boolean {
        val group = identifyFingerprintingGroup(documentId)
        return group == null || group.first() == documentId
    }

    fun identifyFingerprintingGroup(documentId: String): FingerprintGroup? = groups.find {
        documentId in it
    }

    fun removeInconsistentGroups(topic: JudgedTopic<*>): FingerprintGroups {
        return filter { group ->
            val distinctJudgements = group
                    .mapTo(mutableSetOf()) { topic.getDocument(it).judgement }
            distinctJudgements.size == 1
        }
    }
}