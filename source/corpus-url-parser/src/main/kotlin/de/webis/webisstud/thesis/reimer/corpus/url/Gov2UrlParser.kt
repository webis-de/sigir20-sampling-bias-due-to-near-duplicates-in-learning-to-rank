package de.webis.webisstud.thesis.reimer.corpus.url

import de.webis.webisstud.thesis.reimer.data.Data
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.JudgedDocument
import de.webis.webisstud.thesis.reimer.model.trecDocuments
import dev.reimer.kotlin.jvm.ktx.gzipped
import dev.reimer.kotlin.jvm.ktx.toURL
import java.io.BufferedReader
import java.net.URL
import java.util.logging.Level
import java.util.logging.Logger

internal class Gov2UrlParser : UrlParser {
	private companion object {
		private val log = Logger.getLogger(Gov2UrlParser::class.java.simpleName).apply {
			level = Level.WARNING
		}

		private val gov2UrlsFile = Data.webisCephGov2CorpusDir.resolve("gov2-extras/url2id.gz")
	}

	private lateinit var reader: BufferedReader

	override fun parse(): Sequence<Pair<String, URL>> {
		log.info { "Building hostname cache." }
		val documents = Corpus.Gov2.trecDocuments.map(JudgedDocument::id).toSet()
		reader = gov2UrlsFile.inputStream().gzipped().bufferedReader()
		return reader.lineSequence()
				.map { line ->
					line.substringAfterLast(' ', "").also {
						check(it.isNotEmpty())
					} to line.substringBeforeLast(' ')
				}
				.filter { (id, _) -> id in documents }
				.map { (id, url) -> id to url.toURL() }
				.onEach { print('.') }
	}

	override fun close() {
		reader.close()
	}
}