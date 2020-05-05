package de.webis.webisstud.thesis.reimer.clueweb.graph

import de.webis.webisstud.thesis.reimer.corpus.url.urls
import dev.reimer.spark.ktx.*
import org.apache.hadoop.fs.Path

object CorpusLinkCounts : CorpusSparkApp() {

    @JvmStatic
    fun main(vararg args: String) = run(args) { corpus ->

        val directoryPathPrefix = corpus.name.toLowerCase()
        val directoryPath = Path("$directoryPathPrefix-webgraph")
        val nodesPath = Path(directoryPath, "full-nodes.txt")
        val graphPath = Path(directoryPath, "full-graph.txt")
        val inlinkPath = Path(directoryPath, "inlinks")
        val outlinkPath = Path(directoryPath, "outlinks")

        val fileSystem = hadoopConfiguration.fileSystem
        fileSystem.delete(inlinkPath, true)
        fileSystem.delete(outlinkPath, true)

        val url2Id = corpus.urls
                .toRdd(this)

        val url2NodeId = textFile(nodesPath.toString())
                .zipWithIndex()

        val nodeId2Id = url2NodeId
                .join(url2Id)
                .valuesToPair()

        val edges = textFile(graphPath.toString())
                .zipWithIndex()
                .mapToPair { it.flip() }
                .flatMapValues { line ->
                    when {
                        line.isBlank() -> emptyList()
                        else -> {
                            line.splitToSequence(' ')
                                    .map { it.toLong() }
                                    .asIterable()
                        }
                    }
                }
                .mapValues { it - 1 } // Convert to 0-based indices.

        val nodeId2OutlinkCount = edges
                .reduceCountByKey()

        val nodeId2InlinkCount = edges
                .mapToPair { it.flip() }
                .reduceCountByKey()

        val id2InlinkCount = nodeId2Id
                .join(nodeId2InlinkCount)
                .valuesToPair()
        val id2OutlinkCount = nodeId2Id
                .join(nodeId2OutlinkCount)
                .valuesToPair()

        id2InlinkCount.saveAsTextFile(inlinkPath.toString())
        id2OutlinkCount.saveAsTextFile(outlinkPath.toString())
    }
}