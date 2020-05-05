package de.webis.webisstud.thesis.reimer.search.elasticsearch

import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.search.SearchExtBuilder
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.search.collapse.CollapseBuilder
import org.elasticsearch.search.fetch.StoredFieldsContext
import org.elasticsearch.search.fetch.subphase.DocValueFieldsContext
import org.elasticsearch.search.fetch.subphase.FetchSourceContext
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder
import org.elasticsearch.search.rescore.RescorerBuilder
import org.elasticsearch.search.slice.SliceBuilder
import org.elasticsearch.search.suggest.SuggestBuilder

var SearchSourceBuilder.query: QueryBuilder
    get() = query()
    set(value) {
        query(value)
    }

var SearchSourceBuilder.postFilter: QueryBuilder
    get() = postFilter()
    set(value) {
        postFilter(value)
    }

var SearchSourceBuilder.from: Int
    get() = from()
    set(value) {
        from(value)
    }

var SearchSourceBuilder.size: Int
    get() = size()
    set(value) {
        size(value)
    }

var SearchSourceBuilder.minScore: Float
    get() = minScore()
    set(value) {
        minScore(value)
    }

var SearchSourceBuilder.explain: Boolean?
    get() = explain()
    set(value) {
        explain(value)
    }

var SearchSourceBuilder.version: Boolean?
    get() = version()
    set(value) {
        version(value)
    }

var SearchSourceBuilder.timeout: TimeValue
    get() = timeout()
    set(value) {
        timeout(value)
    }

var SearchSourceBuilder.terminateAfter: Int
    get() = terminateAfter()
    set(value) {
        terminateAfter(value)
    }

var SearchSourceBuilder.trackScores: Boolean
    get() = trackScores()
    set(value) {
        trackScores(value)
    }

var SearchSourceBuilder.trackTotalHits: Boolean
    get() = trackTotalHits()
    set(value) {
        trackTotalHits(value)
    }

var SearchSourceBuilder.searchAfter: Array<Any>
    get() = searchAfter()
    set(value) {
        searchAfter(value)
    }

var SearchSourceBuilder.slice: SliceBuilder
    get() = slice()
    set(value) {
        slice(value)
    }

fun SearchSourceBuilder.slice(id: Int, max: Int, block: SliceBuilder.() -> Unit): SearchSourceBuilder =
        slice(SliceBuilder(id, max).apply(block))

var SearchSourceBuilder.collapse: CollapseBuilder
    get() = collapse()
    set(value) {
        collapse(value)
    }

fun SearchSourceBuilder.collapse(field: String, block: CollapseBuilder.() -> Unit): SearchSourceBuilder =
        collapse(CollapseBuilder(field).apply(block))

var SearchSourceBuilder.highlighter: HighlightBuilder
    get() = highlighter()
    set(value) {
        highlighter(value)
    }

var SearchSourceBuilder.suggest: SuggestBuilder
    get() = suggest()
    set(value) {
        suggest(value)
    }

var SearchSourceBuilder.profile: Boolean
    get() = profile()
    set(value) {
        profile(value)
    }

val SearchSourceBuilder.rescores: List<RescorerBuilder<*>>
    get() = rescores()

var SearchSourceBuilder.fetchSource: FetchSourceContext
    get() = fetchSource()
    set(value) {
        fetchSource(value)
    }

var SearchSourceBuilder.storedFields: StoredFieldsContext
    get() = storedFields()
    set(value) {
        storedFields(value)
    }

val SearchSourceBuilder.docValueFields: List<DocValueFieldsContext.FieldAndFormat>
    get() = docValueFields()

val SearchSourceBuilder.scriptFields: List<SearchSourceBuilder.ScriptField>
    get() = scriptFields()

val SearchSourceBuilder.indexBoosts: List<SearchSourceBuilder.IndexBoost>
    get() = indexBoosts()

var SearchSourceBuilder.stats: List<String>
    get() = stats()
    set(value) {
        stats(value)
    }

var SearchSourceBuilder.ext: List<SearchExtBuilder>
    get() = ext()
    set(value) {
        ext(value)
    }
