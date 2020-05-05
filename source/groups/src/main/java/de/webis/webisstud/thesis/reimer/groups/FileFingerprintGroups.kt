package de.webis.webisstud.thesis.reimer.groups

import dev.reimer.serialization.jsonl.JsonL
import kotlinx.serialization.Serializable
import java.io.File

class FileFingerprintGroups(
        file: File
) : FingerprintGroups {

    private companion object {
        private val JSON_L = JsonL()

        private fun parseGroups(file: File): Set<FingerprintGroup> {
            return JSON_L.load(GroupInternal.serializer(), file) { groups ->
                groups.mapTo(sortedSetOf(), GroupInternal::toGroup)
            }
        }
    }

    override val values = LazySet { parseGroups(file) }

    private data class Group(
            override val hash: Long,
            override val ids: List<String>
    ) : FingerprintGroup, List<String> by ids

    @Serializable
    private class GroupInternal(
            val hash: Long,
            val ids: List<String>
    ) {
        fun toGroup(): FingerprintGroup = Group(hash, ids.sorted())
    }
}