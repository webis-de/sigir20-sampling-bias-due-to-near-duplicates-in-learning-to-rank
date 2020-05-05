package de.webis.webisstud.thesis.reimer.groups.canonical

interface CanonicalFingerprintGroup : Map<String, Set<String>> {

    companion object {
        const val UNKNOWN_ID_PREFIX = "UNKNOWN_ID_"
    }

    val hash: Long

    /**
     * Mapping of this groups canonical document IDs to their respective derived document IDs.
     */
    val canonicalGroups: Map<String, Set<String>> get() = this

    /**
     * All document IDs.
     */
    val ids: Set<String>
        get() {
            return canonicalGroups.flatMapTo(mutableSetOf()) { (canonicalId, derivedIds) ->
                val list = if (!canonicalId.startsWith(UNKNOWN_ID_PREFIX)) {
                    mutableListOf(canonicalId)
                } else {
                    mutableListOf()
                }
                list.addAll(derivedIds)
                list
            }
        }

    val canonicalIds: Set<String>
        get() {
            return canonicalGroups
                    .keys
                    .filterNotTo(mutableSetOf()) {
                        it.startsWith(UNKNOWN_ID_PREFIX)
                    }
        }

    val canonicalizedIds: Set<String>
        get() = ids.minus(canonicalIds)

    val isConsistentCanonical: Boolean
        get() = canonicalIds.size <= 1

    val globalCanonical: String
        get() {
            val bySize: Comparator<Map.Entry<String, Set<String>>> = compareBy { (_, ids) -> ids.size }
            val byId: Comparator<Map.Entry<String, Set<String>>> = compareBy { (_, ids) -> ids.size }
            val (canonicalId, _) = checkNotNull(canonicalGroups.maxWith(bySize.reversed().thenComparing(byId)))
            return canonicalId
        }
}