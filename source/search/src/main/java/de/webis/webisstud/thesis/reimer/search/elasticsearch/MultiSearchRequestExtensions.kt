package de.webis.webisstud.thesis.reimer.search.elasticsearch

import org.elasticsearch.action.search.MultiSearchRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.support.IndicesOptions

var MultiSearchRequest.maxConcurrentSearchRequests: Int
    get() = maxConcurrentSearchRequests()
    set(value) {
        maxConcurrentSearchRequests(value)
    }

var MultiSearchRequest.indicesOptions: IndicesOptions
    get() = indicesOptions()
    set(value) {
        indicesOptions(value)
    }

val MultiSearchRequest.requests: List<SearchRequest>
    get() = requests()

inline fun MultiSearchRequest.add(block: SearchRequest.() -> Unit = {}): MultiSearchRequest =
        add(SearchRequest().apply(block))
