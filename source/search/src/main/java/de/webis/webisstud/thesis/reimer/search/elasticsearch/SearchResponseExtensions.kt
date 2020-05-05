package de.webis.webisstud.thesis.reimer.search.elasticsearch

import org.elasticsearch.action.search.SearchResponse

val SearchResponse.totalHits get() = hits.totalHits

fun SearchResponse.isEmpty() = totalHits <= 0
