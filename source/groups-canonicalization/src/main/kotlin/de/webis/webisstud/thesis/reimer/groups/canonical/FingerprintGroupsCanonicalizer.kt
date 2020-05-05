package de.webis.webisstud.thesis.reimer.groups.canonical

import de.webis.webisstud.thesis.reimer.chatnoir.chatNoirPermanentUrl
import de.webis.webisstud.thesis.reimer.corpus.url.urls
import de.webis.webisstud.thesis.reimer.data.dataDir
import de.webis.webisstud.thesis.reimer.groups.FingerprintGroup
import de.webis.webisstud.thesis.reimer.groups.FingerprintGroups
import de.webis.webisstud.thesis.reimer.groups.canonical.CanonicalFingerprintGroup.Companion.UNKNOWN_ID_PREFIX
import de.webis.webisstud.thesis.reimer.groups.fingerprintGroups
import de.webis.webisstud.thesis.reimer.model.Corpus
import dev.reimer.kotlin.jvm.ktx.copy
import dev.reimer.kotlin.jvm.ktx.inverse
import dev.reimer.kotlin.jvm.ktx.mapToMap
import dev.reimer.kotlin.jvm.ktx.prepareNewFile
import dev.reimer.serialization.jsonl.JsonL
import dev.reimer.url.canonical.canonicalize
import kotlinx.coroutines.*
import java.io.File
import java.net.URL

fun main(args: Array<String>) {
    val corpora = args.mapNotNull {
        Corpus.find(it)
    }
    require(corpora.isNotEmpty()) {
        "At least one corpus is required."
    }

    runBlocking {
        for (corpus in corpora) {
            println("Canonicalize $corpus fingerprint groups.")

            val groups = corpus
                    .fingerprintGroups
            val canonicalGroups = groups.groups
                    .asSequence()
                    .map { runBlocking { it.canonicalize(corpus) } }
                    .map { SerializableCanonicalFingerprintGroup(it) }
            JSON_L.save(
                    SerializableCanonicalFingerprintGroup.serializer(),
                    canonicalGroups,
                    corpus.dataDir
                            .resolve("fingerprint-groups-canonical.jsonl")
                            .also(File::prepareNewFile)
            )
        }
    }
}

suspend fun FingerprintGroups.canonicalize(corpus: Corpus) = coroutineScope {
    groups
            .map { group ->
                println("Canonicalize group ${group.hash}.")
                group.canonicalize(corpus)
            }
            .toSet()
            .asCanonicalFingerprintGroups()
}

fun FingerprintGroup.canonicalizeNop(): CanonicalFingerprintGroup {
    val canonicalMap = mapToMap { it to setOf(it) }
    return object : CanonicalFingerprintGroup, Map<String, Set<String>> by canonicalMap {
        override val hash = this@canonicalizeNop.hash
    }
}

suspend fun FingerprintGroup.canonicalize(corpus: Corpus): CanonicalFingerprintGroup = coroutineScope {
    when (corpus) {
        Corpus.NewYorkTimes,
        Corpus.WashingtonPost,
        Corpus.Gov2 -> canonicalizeNop()
        Corpus.ClueWeb09,
        Corpus.ClueWeb12 -> {
            val urls = corpus.urls
            val canonicalMap = ids
                    .map { id ->
                        async {
                            val permanentUrl = corpus.chatNoirPermanentUrl(id)
                            val originalUrl = urls[id]
                            val canonicalUrl = withContext(Dispatchers.IO) {
                                permanentUrl.canonicalize(originalUrl ?: permanentUrl)
                            }
                            if (permanentUrl == canonicalUrl) {
                                id to id
                            } else {
                                id to (urls.findDocumentIdHttpOrHttps(canonicalUrl)
                                        ?: "$UNKNOWN_ID_PREFIX${canonicalUrl}")
                            }.also { print(".") }
                        }
                    }
                    .awaitAll()
                    .toMap()
                    .inverse()
                    .mapValues { (_, ids) -> ids.toSet() }
                    .also { println("\r$it") }
            object : CanonicalFingerprintGroup, Map<String, Set<String>> by canonicalMap {
                override val hash = this@canonicalize.hash
            }
        }
    }
}

private fun Map<String, URL>.findDocumentIdHttpOrHttps(url: URL): String? {
    return findDocumentId(url)
            ?: when (url.protocol) {
                "https" -> findDocumentId(url.copy(protocol = "http"))
                "http" -> findDocumentId(url.copy(protocol = "https"))
                else -> null
            }
}

private fun Map<String, URL>.findDocumentId(url: URL) = entries.find { it.value == url }?.key

private val JSON_L = JsonL()
