package de.webis.webisstud.thesis.reimer.groups.canonical

import de.webis.webisstud.thesis.reimer.model.Corpus

val Corpus.canonicalFingerprintGroups: CanonicalFingerprintGroups
    get() = requireNotNull(CorpusCanonicalFingerprintGroups.values().find { it.corpus == this }) {
        "No canonical fingerprint groups for corpus $this."
    }
