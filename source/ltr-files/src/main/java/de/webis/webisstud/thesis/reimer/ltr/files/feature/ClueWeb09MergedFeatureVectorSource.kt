package de.webis.webisstud.thesis.reimer.ltr.files.feature

import de.webis.webisstud.thesis.reimer.data.dataDir
import de.webis.webisstud.thesis.reimer.ltr.files.source.cached
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.FeatureVector
import de.webis.webisstud.thesis.reimer.model.Resource
import de.webis.webisstud.thesis.reimer.model.TrecTask
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.map
import kotlinx.serialization.serializer

class ClueWeb09MergedFeatureVectorSource(
        task: TrecTask
) : FeatureVectorSource() {

    @Serializable
    class Vector(
            @SerialName("body-tf-similarity") val bodyTf: Float,
            @SerialName("title-tf-similarity") val titleTf: Float,
            @SerialName("anchors-tf-similarity") val anchorsTf: Float,
            @SerialName("main-content-tf-similarity") val mainContentTf: Float,
            @SerialName("body-tf-idf-similarity") val bodyTfIdf: Float,
            @SerialName("title-tf-idf-similarity") val titleTfIdf: Float,
            @SerialName("anchors-tf-idf-similarity") val anchorsTfIdf: Float,
            @SerialName("main-content-tf-idf-similarity") val mainContentTfIdf: Float,
            @SerialName("body-bm25-similarity") val bodyBm25: Float,
            @SerialName("title-bm25-similarity") val titleBm25: Float,
            @SerialName("anchors-bm25-similarity") val anchorsBm25: Float,
            @SerialName("main-content-bm25-similarity") val mainContentBm25: Float,
            @SerialName("body-f2exp-similarity") val bodyF2exp: Float,
            @SerialName("title-f2exp-similarity") val titleF2exp: Float,
            @SerialName("anchors-f2exp-similarity") val anchorsF2exp: Float,
            @SerialName("main-content-f2exp-similarity") val mainContentF2exp: Float,
            @SerialName("body-f2log-similarity") val bodyF2log: Float,
            @SerialName("title-f2log-similarity") val titleF2log: Float,
            @SerialName("anchors-f2log-similarity") val anchorsF2log: Float,
            @SerialName("main-content-f2log-similarity") val mainContentF2log: Float,
            @SerialName("body-ql-similarity") val bodyQl: Float,
            @SerialName("title-ql-similarity") val titleQl: Float,
            @SerialName("anchors-ql-similarity") val anchorsQl: Float,
            @SerialName("main-content-ql-similarity") val mainContentQl: Float,
            @SerialName("body-qljm-similarity") val bodyQljm: Float,
            @SerialName("title-qljm-similarity") val titleQljm: Float,
            @SerialName("anchors-qljm-similarity") val anchorsQljm: Float,
            @SerialName("main-content-qljm-similarity") val mainContentQljm: Float,
            @SerialName("body-pl2-similarity") val bodyPl2: Float,
            @SerialName("title-pl2-similarity") val titlePl2: Float,
            @SerialName("anchors-pl2-similarity") val anchorsPl2: Float,
            @SerialName("main-content-pl2-similarity") val mainContentPl2: Float,
            @SerialName("body-spl-similarity") val bodySpl: Float,
            @SerialName("title-spl-similarity") val titleSpl: Float,
            @SerialName("anchors-spl-similarity") val anchorsSpl: Float,
            @SerialName("main-content-spl-similarity") val mainContentSpl: Float
    )

    private companion object {

        private val INLINK_RESOURCE = Resource("/clueweb09/inlinks.txt", ClueWeb09MergedFeatureVectorSource::class)
        private val OUTLINK_RESOURCE = Resource("/clueweb09/outlinks.txt", ClueWeb09MergedFeatureVectorSource::class)
        private val RETRIEVAL_FEATURE_RESOURCE = Resource("/clueweb09/feature-vectors.json", ClueWeb09MergedFeatureVectorSource::class)
        private val RETRIEVAL_FEATURE_SERIALIZER =
                (String.serializer() to (String.serializer() to Vector.serializer()).map).map
        private val JSON = Json(JsonConfiguration.Stable)

        private fun Sequence<FeatureVector>.toMap(): Map<String, Map<String, List<Float>>> {
            val map = mutableMapOf<String, MutableMap<String, List<Float>>>()
            for (vector in this) {
                val topicMap = map.getOrPut(vector.topicId) { mutableMapOf() }
                topicMap[vector.documentId] = vector.features
            }
            return map
        }

        private fun Resource.parseLinkCounts(): Map<String, Int> {
            return inputStream().bufferedReader().useLines { lines ->
                lines.map { line ->
                    line.subSequence(1, line.length - 1)
                            .split(',')
                            .let { it[0] to it[1].toInt() }
                }.toMap()
            }
        }

        private fun Resource.parseRetrievalFeatures() =
                JSON.parse(RETRIEVAL_FEATURE_SERIALIZER, inputStream().bufferedReader().readText())
                        .mapValues { (_, map) ->
                            map.mapValues { (_, features) ->
                                with(features) {
                                    listOf(
                                            bodyTf,
                                            titleTf,
                                            anchorsTf,
                                            mainContentTf,
                                            bodyTfIdf,
                                            titleTfIdf,
                                            anchorsTfIdf,
                                            mainContentTfIdf,
                                            bodyBm25,
                                            titleBm25,
                                            anchorsBm25,
                                            mainContentBm25,
                                            bodyF2exp,
                                            titleF2exp,
                                            anchorsF2exp,
                                            mainContentF2exp,
                                            bodyF2log,
                                            titleF2log,
                                            anchorsF2log,
                                            mainContentF2log,
                                            bodyQl,
                                            titleQl,
                                            anchorsQl,
                                            mainContentQl,
                                            bodyQljm,
                                            titleQljm,
                                            anchorsQljm,
                                            mainContentQljm,
                                            bodyPl2,
                                            titlePl2,
                                            anchorsPl2,
                                            mainContentPl2,
                                            bodySpl,
                                            titleSpl,
                                            anchorsSpl,
                                            mainContentSpl
                                    )
                                }
                            }
                        }
    }

    private val taskDir = task.dataDir
    private val searchFeatureFile = taskDir.resolve("es-features.fv")
    private val searchFeatureSource =
            ElasticsearchFeatureVectorSource(task).cached(searchFeatureFile)

    init {
        require(task.corpus == Corpus.ClueWeb09) {
            "Unsupported corpus."
        }
    }

    private val list by lazy {
        println("Load features <- Elasticsearch features + link counts + retrieval vectors") // TODO
        val searchFeatures = searchFeatureSource.toMap()
        val inlinks = INLINK_RESOURCE.parseLinkCounts()
        val outlinks = OUTLINK_RESOURCE.parseLinkCounts()
        val retrievalFeatures = RETRIEVAL_FEATURE_RESOURCE.parseRetrievalFeatures()

        task.topics.flatMap { topic ->
            topic.documents.map { document ->
                val searchFeature = searchFeatures[topic.id]
                        ?.get(document.id)
                        ?.also { check(it.size == 4) { "Expecting exactly 4 features retrieved from Elasticsearch." } }
                        ?: listOf(
                                FeatureVector.UNKNOWN,
                                FeatureVector.UNKNOWN,
                                FeatureVector.UNKNOWN,
                                FeatureVector.UNKNOWN
                        )
                val inlink = inlinks[document.id]
                        ?.toFloat()
                        ?: FeatureVector.UNKNOWN
                val outlink = outlinks[document.id]
                        ?.toFloat()
                        ?: FeatureVector.UNKNOWN
                val linkFeature = listOf(inlink, outlink)
                val retrievalFeature = retrievalFeatures
                        .getValue(topic.id)
                        .getValue(document.id)
                val features = listOf(searchFeature, linkFeature, retrievalFeature).flatten()
                FeatureVector(document.id, topic.id, document.judgement.toFloat(), features)
            }
        }
    }

    override fun iterator() = list.iterator()
}
