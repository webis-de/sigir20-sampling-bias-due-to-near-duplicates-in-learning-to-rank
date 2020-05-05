package de.webis.webisstud.thesis.reimer.groups

class FilteredFingerprintGroups(
        unfiltered: FingerprintGroups,
        predicate: (FingerprintGroup) -> Boolean
) : FingerprintGroups {
    override val values: Set<FingerprintGroup> = unfiltered.values.filterTo(sortedSetOf(), predicate)
}