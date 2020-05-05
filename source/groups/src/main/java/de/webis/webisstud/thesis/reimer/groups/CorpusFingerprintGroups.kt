package de.webis.webisstud.thesis.reimer.groups

import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.resource
import java.io.File

internal enum class CorpusFingerprintGroups(val corpus: Corpus, file: File) : FingerprintGroups by FileFingerprintGroups(file) {
    ClueWeb09(Corpus.ClueWeb09, Files.CLUE_WEB_09_FILE),
    ClueWeb12(Corpus.ClueWeb12, Files.CLUE_WEB_12_FILE),
    Gov2(Corpus.Gov2, Files.GOV2_FILE);

    private object Files {
        val CLUE_WEB_09_FILE = resource("/clueweb09/fingerprint-groups.jsonl").file()
        val CLUE_WEB_12_FILE = resource("/clueweb12/fingerprint-groups.jsonl").file()
        val GOV2_FILE = resource("/gov2/fingerprint-groups.jsonl").file()
    }
}