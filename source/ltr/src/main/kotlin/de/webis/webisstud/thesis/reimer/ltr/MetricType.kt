package de.webis.webisstud.thesis.reimer.ltr

import ciir.umass.edu.learning.RankList
import ciir.umass.edu.metric.*
import java.io.Serializable

sealed class MetricType : Serializable {

	abstract val id: String

	abstract val scorer: MetricScorer

	open class Best(private val cutoff: Int = -1) : MetricType() {
		final override val id = "best${cutoff.takeIf { it >= 0 }?.let { "@$it" } ?: ""}"
		final override val scorer: MetricScorer get() = BestAtKScorer().apply { if (cutoff >= 0) k = cutoff }

		companion object : Best()
	}

	/**
	 * Discounted Cumulative Gain
	 */
	open class Dcg(private val cutoff: Int = -1) : MetricType() {
		final override val id = "dcg${cutoff.takeIf { it >= 0 }?.let { "@$it" } ?: ""}"
		final override val scorer: MetricScorer get() = DCGScorer().apply { if (cutoff >= 0) k = cutoff }

		companion object : Dcg()
	}

	/**
	 * Expected Reciprocal Rank
	 */
	open class Err(private val cutoff: Int = -1) : MetricType() {
		final override val id = "err${cutoff.takeIf { it >= 0 }?.let { "@$it" } ?: ""}"
		final override val scorer: MetricScorer get() = ERRScorer().apply { if (cutoff >= 0) k = cutoff }

		companion object : Err()
	}

    /**
     * Mean Average Precision
     */
    object Map : MetricType() {
        override val id = "map"
        override val scorer: MetricScorer get() = APScorer()
    }

	/**
	 * Normalized Discounted Cumulative Gain
	 */
	open class Ndcg(private val cutoff: Int = -1) : MetricType() {
		final override val id = "ndcg${cutoff.takeIf { it >= 0 }?.let { "@$it" } ?: ""}"
		final override val scorer: MetricScorer get() = NDCGScorer().apply { if (cutoff >= 0) k = cutoff }

		companion object : Ndcg()
	}

	open class Precision(private val cutoff: Int = -1) : MetricType() {
		final override val id = "precision${cutoff.takeIf { it >= 0 }?.let { "@$it" } ?: ""}"
		final override val scorer: MetricScorer get() = PrecisionScorer().apply { if (cutoff >= 0) k = cutoff }

		companion object : Precision()
	}

	/**
	 * Reciprocal Rank
	 */
	object Reciprocal : MetricType() {
		override val id = "reciprocal"
		override val scorer: MetricScorer get() = ReciprocalRankScorer()
	}

	fun score(run: Iterable<JudgedRunLine>) = scorer.score(RankList(run.map(JudgedRunLine::asDataPoint)))
}
