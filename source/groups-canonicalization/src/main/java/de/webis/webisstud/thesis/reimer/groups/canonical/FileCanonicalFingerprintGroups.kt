package de.webis.webisstud.thesis.reimer.groups.canonical

import dev.reimer.serialization.jsonl.JsonL
import java.io.File

class FileCanonicalFingerprintGroups(
        file: File
) : CanonicalFingerprintGroups, AbstractMap<Long, CanonicalFingerprintGroup>() {

    private companion object {
        private val JSON_L = JsonL()

        private fun parseGroups(file: File): Set<CanonicalFingerprintGroup> {
            return JSON_L.load(
                    SerializableCanonicalFingerprintGroup.serializer(),
                    file
            ) { groups ->
                groups.mapTo(mutableSetOf(), SerializableCanonicalFingerprintGroup::toGroup)
            }
        }
    }

    override val entries: Set<Map.Entry<Long, CanonicalFingerprintGroup>> by lazy {
        parseGroups(file).mapTo(mutableSetOf()) {
            object : Map.Entry<Long, CanonicalFingerprintGroup> {
                override val key = it.hash
                override val value = it
            }
        }
    }
}