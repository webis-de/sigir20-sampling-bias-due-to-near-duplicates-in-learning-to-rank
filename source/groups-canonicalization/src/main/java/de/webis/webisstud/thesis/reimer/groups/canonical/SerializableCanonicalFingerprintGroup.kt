package de.webis.webisstud.thesis.reimer.groups.canonical

import kotlinx.serialization.Serializable

@Serializable
data class SerializableCanonicalFingerprintGroup(
        val hash: Long,
        val ids: List<CanonicalId>
) {
    constructor(groups: CanonicalFingerprintGroup) : this(
            groups.hash,
            groups.canonicalGroups
                    .entries
                    .flatMap { (canonicalId, ids) ->
                        ids.map {
                            CanonicalId(it, canonicalId)
                        }
                    }
    )

    @Serializable
    data class CanonicalId(
            val id: String,
            val canonicalId: String
    )

    fun toGroup(): CanonicalFingerprintGroup {
        val ids = ids
                .groupBy { it.canonicalId }
                .mapValues { (_, ids) ->
                    ids.mapTo(mutableSetOf()) { it.id }
                }
        return object : CanonicalFingerprintGroup, Map<String, Set<String>> by ids {
            override val hash = this@SerializableCanonicalFingerprintGroup.hash
        }
    }
}