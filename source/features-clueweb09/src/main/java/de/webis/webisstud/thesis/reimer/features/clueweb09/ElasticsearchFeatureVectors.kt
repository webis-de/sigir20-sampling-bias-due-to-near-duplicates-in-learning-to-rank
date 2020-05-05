package de.webis.webisstud.thesis.reimer.features.clueweb09

import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.FeatureVector
import de.webis.webisstud.thesis.reimer.model.trecDocuments
import de.webis.webisstud.thesis.reimer.search.WebisCluster.Elasticsearch.Fields.WARC_TARGET_URI
import de.webis.webisstud.thesis.reimer.search.index
import de.webis.webisstud.thesis.reimer.search.isSearchable
import de.webis.webisstud.thesis.reimer.search.loadSource

class ElasticsearchFeatureVectors(
        corpus: Corpus
) : AbstractList<FeatureVector>() {

    init {
        require(corpus.isSearchable) { "No searchable index found for corpus." }
    }

    override val size get() = list.size

    override fun get(index: Int) = list[index]

    private val list by lazy {
        println("Load features <- Elasticsearch index '${corpus.index}'")
        val features = corpus.loadFeatures()
        corpus.trecDocuments.mapNotNull { document ->
                    features[document.id]?.let {
                        FeatureVector(
                                document.id,
                                document.topic.id,
                                document.judgement.toFloat(),
                                it
                        )
                    }
                }.toList()
    }

    override fun iterator() = list.iterator()

    private fun Corpus.loadFeatures(): Map<String, List<Float>> {
        return loadSource(WARC_TARGET_URI, "page_rank", "spam_rank")
                .mapValues { (_, source) ->
                    val urlExcludingProtocol = (source[0] as? String)
                            ?.substringAfter("://")
                    val urlLength = urlExcludingProtocol
                            ?.length
                            ?.toFloat()
                            ?: FeatureVector.UNKNOWN
                    val urlSlashCount = urlExcludingProtocol
                            ?.count { it == '/' }
                            ?.toFloat()
                            ?: FeatureVector.UNKNOWN
                    val pageRank = (source[1] as? Number)
                            ?.toFloat()
                            ?: FeatureVector.UNKNOWN
                    val spamRank = (source[2] as? Number)
                            ?.toFloat()
                            ?: FeatureVector.UNKNOWN
                    listOf(
                            pageRank,
                            spamRank,
                            urlSlashCount,
                            urlLength
                    )
                }
    }
}
