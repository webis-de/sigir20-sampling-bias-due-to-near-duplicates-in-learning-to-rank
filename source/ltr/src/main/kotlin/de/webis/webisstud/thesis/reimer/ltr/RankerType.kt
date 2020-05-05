package de.webis.webisstud.thesis.reimer.ltr

import ciir.umass.edu.learning.DataPoint
import ciir.umass.edu.learning.LinearRegRank
import ciir.umass.edu.learning.Ranker
import ciir.umass.edu.learning.tree.LambdaMART
import ciir.umass.edu.learning.tree.MART
import ciir.umass.edu.learning.tree.RFRanker
import de.webis.webisstud.thesis.reimer.model.Corpus
import java.io.Serializable

sealed class RankerType : Serializable {

    abstract val id: String

    abstract val ranker: Ranker

    abstract val isDeterministic: Boolean

    object AdaRank : RankerType() {
        override val id = "ada-rank"
        override val ranker: Ranker get() = ciir.umass.edu.learning.boosting.AdaRank()
        override val isDeterministic = false // TODO: Is it really non-deterministic?
    }

    object CoorAscent : RankerType() {
        override val id = "coordinate-ascent"
        override val ranker: Ranker get() = ciir.umass.edu.learning.CoorAscent()
        override val isDeterministic = false
    }

    object LambdaMart : RankerType() {
        override val id = "lambda-mart"
        override val ranker: Ranker get() = LambdaMART()
        override val isDeterministic = false // TODO: Is it really non-deterministic?
    }

    object LambdaRank : RankerType() {
        override val id = "lambda-rank"
        override val ranker: Ranker get() = ciir.umass.edu.learning.neuralnet.LambdaRank()
        override val isDeterministic = false
    }

    object LinearRegression : RankerType() {
        override val id = "linear-regression"
        override val ranker: Ranker get() = LinearRegRank()
        override val isDeterministic = true
    }

    object ListNet : RankerType() {
        override val id = "list-net"
        override val ranker: Ranker get() = ciir.umass.edu.learning.neuralnet.ListNet()
        override val isDeterministic = false
    }

    object Mart : RankerType() {
        override val id = "mart"
        override val ranker: Ranker get() = MART()
        override val isDeterministic = false // TODO: Is it really non-deterministic?
    }

    object RandomForrest : RankerType() {
        override val id = "random-forrest"
        override val ranker: Ranker get() = RFRanker()
        override val isDeterministic = false
    }

    object RankBoost : RankerType() {
        override val id = "rank-boost"
        override val ranker: Ranker get() = ciir.umass.edu.learning.boosting.RankBoost()
        override val isDeterministic = false // TODO: Is it really non-deterministic?
    }

    object RankNet : RankerType() {
        override val id = "rank-net"
        override val ranker: Ranker get() = ciir.umass.edu.learning.neuralnet.RankNet()
        override val isDeterministic = false
    }

    data class Bm25(val corpus: Corpus) : RankerType() {
        override val id = "bm25"
        override val ranker: Ranker = Bm25Ranker(id, corpus)
        override val isDeterministic = true

        private class Bm25Ranker(
                private val id: String,
                private val corpus: Corpus
        ) : Ranker(), Serializable {
            override fun loadFromString(fullText: String) {}
            override fun createNew() = this
            override fun toString() = ""
            override fun init() {}
            override fun name() = id
            override fun learn() {}
            override fun printParameters() {}
            override fun model() = ""
            override fun eval(p: DataPoint) = p.documentBm25(corpus).toDouble()
        }
    }
}
