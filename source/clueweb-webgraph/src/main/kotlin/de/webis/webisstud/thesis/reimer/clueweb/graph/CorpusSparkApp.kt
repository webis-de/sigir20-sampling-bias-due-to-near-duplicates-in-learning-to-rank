package de.webis.webisstud.thesis.reimer.clueweb.graph

import de.webis.webisstud.thesis.reimer.model.Corpus
import dev.reimer.spark.ktx.*
import org.apache.log4j.Level
import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext

abstract class CorpusSparkApp {
    open val defaultAppName: String? = null
    open val logLevel: Level = Level.INFO

    open fun SparkConf.configure() {}

    protected fun run(args: Array<out String>, block: JavaSparkContext.(corpus: Corpus) -> Unit) {
        val corpus = Corpus.valueOfCaseInsensitive(args[0])

        setSparkLogLevel(logLevel)
        spark {
            configure()

            if (!hasMaster) master = "local[*]"
            val defaultAppName = defaultAppName
            if (!hasAppName && defaultAppName != null) appName = defaultAppName

            context {
                block(corpus)
            }
        }
    }
}