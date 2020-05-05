package de.webis.webisstud.thesis.reimer.groups

class CompositeFingerprintGroups(
        vararg allGroups: FingerprintGroups
) : FingerprintGroups {
    override val values: Set<FingerprintGroup> = allGroups.flatMapTo(sortedSetOf()) { it.values }
}
