package de.webis.webisstud.thesis.reimer.corpus.url

import dev.reimer.serialization.jsonl.JsonL
import kotlinx.serialization.Serializable
import java.io.BufferedReader
import java.io.Closeable
import java.io.File
import java.net.URL

interface UrlParser : Closeable {

	companion object {
		private val JSON_L = JsonL()

		internal fun parse(file: File) = JsonParser(file).parse().toMap()
	}

	private class JsonParser(private val file: File) : UrlParser {

		private lateinit var reader: BufferedReader

		override fun parse(): Sequence<Pair<String, URL>> {
			reader = file.bufferedReader()
			return JSON_L.parse(DocumentUrl.serializer(), reader.lineSequence())
					.map { it.id to it.url }
		}

		override fun close() {}
	}

	fun save(file: File) {
		val documentUrls = parse()
				.map { (id, url) -> DocumentUrl(id, url) }
		JSON_L.save(DocumentUrl.serializer(), documentUrls, file)
	}

	fun parse(): Sequence<Pair<String, URL>>

	@Serializable
	private data class DocumentUrl(
			val id: String,
			@Serializable(with = UrlSerializer::class) val url: URL
	)
}