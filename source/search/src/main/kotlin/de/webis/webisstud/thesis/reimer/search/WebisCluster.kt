package de.webis.webisstud.thesis.reimer.search

import com.janheinrichmerker.elasticsearch.kotlin.dsl.rest.restHighLevelClientOf
import org.apache.http.HttpHost

object WebisCluster {

    object Elasticsearch {
        private val HOST: HttpHost = HttpHost.create("http://betaweb129.medien.uni-weimar.de:9200")
        val CLIENT = restHighLevelClientOf(HOST)
        const val MAX_REQUEST_SIZE = 10_000

        enum class Index(
                val indexName: String,
                val defaultType: String
        ) {
            WARC_CLUEWEB_09("webis_warc_clueweb09_003", "warcrecord"),
            WARC_CLUEWEB_12("webis_warc_clueweb12_011", "warcrecord")
        }

        object Fields {
            const val WARC_TARGET_URI = "warc_target_uri"
            const val WARC_TREC_ID = "warc_trec_id"
        }
    }


}