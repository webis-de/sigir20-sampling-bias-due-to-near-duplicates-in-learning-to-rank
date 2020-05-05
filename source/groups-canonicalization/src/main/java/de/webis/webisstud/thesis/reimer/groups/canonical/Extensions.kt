package de.webis.webisstud.thesis.reimer.groups.canonical

fun Map<Long, CanonicalFingerprintGroup>.asCanonicalFingerprintGroups(): CanonicalFingerprintGroups =
        object : CanonicalFingerprintGroups, Map<Long, CanonicalFingerprintGroup> by this {}

fun Sequence<CanonicalFingerprintGroup>.asCanonicalFingerprintGroups() =
        map { it.hash to it }.toMap().asCanonicalFingerprintGroups()

fun Set<CanonicalFingerprintGroup>.asCanonicalFingerprintGroups() =
        asSequence().asCanonicalFingerprintGroups()
