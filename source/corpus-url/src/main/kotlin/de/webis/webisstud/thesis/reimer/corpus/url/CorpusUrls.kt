package de.webis.webisstud.thesis.reimer.corpus.url

import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.resource
import java.io.File
import java.net.URL

internal enum class CorpusUrls(val corpus: Corpus, file: File) : Map<String, URL> by UrlParser.parse(file) {
    ClueWeb09(Corpus.ClueWeb09, Files.CLUE_WEB_09_FILE),
    ClueWeb12(Corpus.ClueWeb12, Files.CLUE_WEB_12_FILE),
    Gov2(Corpus.Gov2, Files.GOV_2_FILE);

    private object Files {
        val CLUE_WEB_09_FILE = resource("/clueweb09/urls.jsonl").file()
        val CLUE_WEB_12_FILE = resource("/clueweb12/urls.jsonl").file()
        val GOV_2_FILE = resource("/gov2/urls.jsonl").file()
    }
}