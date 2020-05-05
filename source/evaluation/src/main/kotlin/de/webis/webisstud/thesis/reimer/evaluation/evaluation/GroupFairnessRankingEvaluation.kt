package de.webis.webisstud.thesis.reimer.evaluation.evaluation

import de.webis.webisstud.thesis.reimer.evaluation.RankingEvaluation
import de.webis.webisstud.thesis.reimer.ltr.JudgedRunLine
import de.webis.webisstud.thesis.reimer.math.indicator
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.Document
import dev.reimer.kotlin.jvm.ktx.productByDouble
import kotlinx.serialization.serializer
import kotlin.math.pow
import kotlin.math.sqrt

abstract class GroupFairnessRankingEvaluation<T> : RankingEvaluation<Double> {

	abstract fun buildGroups(metadata: Set<Document.Metadata>, corpus: Corpus): Map<Document.Metadata, T>

	final override val serializer get() = Double.serializer()

	open val continuationProbability: Double = 0.5 // From TREC 2019 Fair Ranking Track
	open val stoppingProbabilityFactor: Double = 0.7 // From TREC 2019 Fair Ranking Track

	private val JudgedRunLine.binaryRelevance
		get() = when {
			relevance.judgement > 0 -> 1
			else -> 0
		}

	private val JudgedRunLine.stoppingProbability get() = stoppingProbabilityFactor * binaryRelevance

	private fun List<JudgedRunLine>.authorExposure(author: T, lookup: Map<Document.Metadata, T>): Double {
		return withIndex().sumByDouble { (index, runLine) ->
			continuationProbability.pow(index) *
					take(index).productByDouble { precedingRun ->
						1 - precedingRun.stoppingProbability
					} * indicator(lookup[runLine] == author)
		}
	}

	private fun List<JudgedRunLine>.authorRelevance(author: T, lookup: Map<Document.Metadata, T>): Double {
		return filter { lookup[it] == author }.sumByDouble { runLine ->
			runLine.stoppingProbability
		}
	}

	private fun List<JudgedRunLine>.groupExposure(author: T, lookup: Map<Document.Metadata, T>, globalExposure: Double): Double {
		return authorExposure(author, lookup) / globalExposure
	}

	private fun List<JudgedRunLine>.groupRelevance(author: T, lookup: Map<Document.Metadata, T>, globalRelevance: Double): Double {
		return authorRelevance(author, lookup) / globalRelevance
	}

	private fun List<JudgedRunLine>.normalizedGroupExposure(author: T, lookup: Map<Document.Metadata, T>, globalExposure: Double, globalRelevance: Double): Double {
		return groupExposure(author, lookup, globalExposure) - groupRelevance(author, lookup, globalRelevance)
	}

	private fun List<JudgedRunLine>.fairExposure(lookup: Map<Document.Metadata, T>): Double {
		val groups = lookup.values.toSet()
		check(groups.isNotEmpty())

		val globalExposure = groups.sumByDouble { authorExposure(it, lookup) }
		val globalRelevance = groups.sumByDouble { authorRelevance(it, lookup) }

		if (globalRelevance == 0.0) {
			// We can't calculate fairness, if we don't see any relevant documents.
			return -1.0
		}

		return groups.sumByDouble { group ->
			normalizedGroupExposure(group, lookup, globalExposure, globalRelevance)
					.pow(2)
					.also { check(!it.isNaN()) }
		}.let(::sqrt)
	}

	final override fun evaluate(run: List<JudgedRunLine>, corpus: Corpus): Double {
		val groupsLookup = buildGroups(run.toSet(), corpus)
		return run.fairExposure(groupsLookup)
	}
}