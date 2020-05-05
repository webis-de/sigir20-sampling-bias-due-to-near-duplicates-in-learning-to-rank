package de.webis.webisstud.thesis.reimer.groups

import de.webis.webisstud.thesis.reimer.model.Corpus

val Corpus.fingerprintGroups: FingerprintGroups
    get() = requireNotNull(CorpusFingerprintGroups.values().find { it.corpus == this }) {
        "No fingerprint groups for corpus $this."
    }
