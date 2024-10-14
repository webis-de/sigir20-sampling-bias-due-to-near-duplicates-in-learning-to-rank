package de.webis.webisstud.thesis.reimer.search

import com.janheinrichmerker.elasticsearch.kotlin.dsl.rest.search
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.TopicTask
import de.webis.webisstud.thesis.reimer.model.trecDocuments
import de.webis.webisstud.thesis.reimer.search.WebisCluster.Elasticsearch.Fields.WARC_TREC_ID
import de.webis.webisstud.thesis.reimer.search.elasticsearch.query
import de.webis.webisstud.thesis.reimer.search.elasticsearch.searchType
import de.webis.webisstud.thesis.reimer.search.elasticsearch.size
import de.webis.webisstud.thesis.reimer.search.elasticsearch.source
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.index.query.QueryBuilders

fun TopicTask<*, *>.loadSource(vararg fields: String): Map<String, Array<Any?>> {
    val documents = topics
            .asSequence()
            .flatMap { it.documents.asSequence() }
            .map { it.id }
            .toSet()
    return loadSource(documents, corpus.index.indexName, *fields)
}

fun Corpus.loadSource(vararg fields: String): Map<String, Array<Any?>> {
    val documents = trecDocuments
            .map { it.id }
            .toSet()
    return loadSource(documents, index.indexName, *fields)
}

fun loadSource(
        documentIds: Set<String>,
        index: String,
        vararg fields: String
): Map<String, Array<Any?>> {
    return documentIds
            .asSequence()
            .chunked(WebisCluster.Elasticsearch.MAX_REQUEST_SIZE)
            .flatMap { ids ->
                println("Load chunk from Elasticsearch (${ids.size} documents).") // TODO
                val result = WebisCluster.Elasticsearch.CLIENT.search {
                    indices(index)
                    searchType = SearchType.DFS_QUERY_THEN_FETCH
                    source {
                        size = WebisCluster.Elasticsearch.MAX_REQUEST_SIZE
                        query = QueryBuilders.termsQuery(WARC_TREC_ID, *(ids.toTypedArray()))
                        fetchSource(arrayOf(WARC_TREC_ID, *fields), arrayOf())
                    }
                }
                result.hits
                        .asSequence()
                        .map { hit ->
                            val source: Map<String, Any> = hit.sourceAsMap
                            val id = source[WARC_TREC_ID] as String
                            val fieldValues = fields.map { source[it] }.toTypedArray()
                            id to fieldValues
                        }
            }
            .toMap()
}
