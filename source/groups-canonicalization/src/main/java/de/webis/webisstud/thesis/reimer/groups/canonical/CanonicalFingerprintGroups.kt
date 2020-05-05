package de.webis.webisstud.thesis.reimer.groups.canonical

import dev.reimer.serialization.jsonl.JsonL
import java.io.File

interface CanonicalFingerprintGroups : Map<Long, CanonicalFingerprintGroup> {
    val groups: Set<CanonicalFingerprintGroup>
        get() {
            val groups = values.toSet()
            check(groups.size == values.size)
            return groups
        }

    fun save(file: File) {
        return JSON_L.save(
                SerializableCanonicalFingerprintGroup.serializer(),
                groups.asSequence().map(::SerializableCanonicalFingerprintGroup),
                file
        )
    }

    companion object {
        private val JSON_L = JsonL()
    }
}
