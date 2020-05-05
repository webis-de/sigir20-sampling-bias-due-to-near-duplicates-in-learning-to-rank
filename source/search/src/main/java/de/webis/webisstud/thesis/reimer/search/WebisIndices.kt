package de.webis.webisstud.thesis.reimer.search

import de.webis.webisstud.thesis.reimer.model.Corpus

val Corpus.isSearchable: Boolean
    get() {
        return when (this) {
            Corpus.ClueWeb09,
            Corpus.ClueWeb12 -> true
            else -> false
        }
    }

val Corpus.index: WebisCluster.Elasticsearch.Index
    get() {
        return when (this) {
            Corpus.ClueWeb09 -> WebisCluster.Elasticsearch.Index.WARC_CLUEWEB_09
            Corpus.ClueWeb12 -> WebisCluster.Elasticsearch.Index.WARC_CLUEWEB_12
            else -> error("No index in Webis for corpus $this.")
        }
    }
