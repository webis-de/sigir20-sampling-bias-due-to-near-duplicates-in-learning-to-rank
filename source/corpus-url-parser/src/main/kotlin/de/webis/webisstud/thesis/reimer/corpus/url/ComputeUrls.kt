package de.webis.webisstud.thesis.reimer.corpus.url

import de.webis.webisstud.thesis.reimer.data.dataDir
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.search.isSearchable
import dev.reimer.kotlin.jvm.ktx.prepareNewFile
import java.io.File

fun main(args: Array<String>) {
	val corpus = Corpus.valueOfCaseInsensitive(args[0])
	val parser = if (corpus.isSearchable) {
		SearchableCorpusUrlParser(corpus)
	} else {
		when (corpus) {
			Corpus.Gov2 -> Gov2UrlParser()
			else -> error("No method for computing URLs for corpus $corpus.")
		}
	}
	parser.save(corpus.dataDir.resolve("urls.jsonl").also(File::prepareNewFile))
}