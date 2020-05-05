package de.webis.webisstud.thesis.reimer.corpus.url

import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.Document
import de.webis.webisstud.thesis.reimer.model.JudgedDocument
import de.webis.webisstud.thesis.reimer.model.trecDocuments
import de.webis.webisstud.thesis.reimer.search.WebisCluster.Elasticsearch.Fields.WARC_TARGET_URI
import de.webis.webisstud.thesis.reimer.search.index
import de.webis.webisstud.thesis.reimer.search.loadSource
import dev.reimer.kotlin.jvm.ktx.toURL
import java.net.URL
import java.util.logging.Level
import java.util.logging.Logger

internal class SearchableCorpusUrlParser(
		private val documents: Collection<Document>,
		private val index: String
) : UrlParser {

	constructor(corpus: Corpus, predicate: (JudgedDocument) -> Boolean = { true }) : this(corpus.trecDocuments.filter(predicate).toSet(), corpus.index.indexName)

	private companion object {
		private val log = Logger.getLogger(SearchableCorpusUrlParser::class.java.simpleName).apply {
			level = Level.WARNING
		}
	}

	override fun parse(): Sequence<Pair<String, URL>> {
		log.info { "Building hostname cache." }
		val ids: Set<String> = documents.mapTo(mutableSetOf()) { it.id }
		return loadSource(ids, index, WARC_TARGET_URI)
				.asSequence()
				.mapNotNull { (id, fields) ->
					val uri = fields[0] as? String
					if (uri != null) {
						id to uri.toURL()
					} else null
				}
	}

	override fun close() {}
}