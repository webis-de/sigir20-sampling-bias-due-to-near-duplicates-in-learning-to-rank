package de.webis.webisstud.thesis.reimer.search.elasticsearch

import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.action.support.IndicesOptions
import org.elasticsearch.search.Scroll
import org.elasticsearch.search.builder.SearchSourceBuilder

var SearchRequest.indices: Array<String>
    get() = indices()
    set(value) {
        indices(*value)
    }

var SearchRequest.indicesOptions: IndicesOptions
    get() = indicesOptions()
    set(value) {
        indicesOptions(value)
    }

var SearchRequest.types: Array<String>
    get() = types()
    set(value) {
        types(*value)
    }

var SearchRequest.routing: String?
    get() = routing()
    set(value) {
        routing(value)
    }

var SearchRequest.preference: String?
    get() = preference()
    set(value) {
        preference(value)
    }

var SearchRequest.searchType: SearchType
    get() = searchType()
    set(value) {
        searchType(value)
    }

var SearchRequest.source: SearchSourceBuilder
    get() = source()
    set(value) {
        source(value)
    }

inline fun SearchRequest.source(block: SearchSourceBuilder.() -> Unit = {}): SearchRequest =
        source(SearchSourceBuilder().apply(block))

var SearchRequest.scroll: Scroll?
    get() = scroll()
    set(value) {
        scroll(value)
    }

var SearchRequest.requestCache: Boolean?
    get() = requestCache()
    set(value) {
        requestCache(value)
    }

var SearchRequest.allowPartialSearchResults: Boolean?
    get() = allowPartialSearchResults()
    set(value) {
        value?.let { allowPartialSearchResults(it) }
    }
