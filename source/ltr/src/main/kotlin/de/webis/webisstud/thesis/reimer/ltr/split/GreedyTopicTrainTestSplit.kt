package de.webis.webisstud.thesis.reimer.ltr.split

import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.JudgedDocument
import de.webis.webisstud.thesis.reimer.model.JudgedTopic
import de.webis.webisstud.thesis.reimer.model.trecTopics
import kotlin.math.roundToInt

abstract class GreedyTopicTrainTestSplit(corpus: Corpus) : TopicTrainTestSplit() {

	private companion object {
		private const val defaultGlobalProportion = 1.0
		private const val defaultTrainingProportion = 1.0 / 3.0
	}

	protected open val globalProportion = defaultGlobalProportion
	private val globalNormalizedProportion get() = globalProportion.coerceIn(0.0, 1.0)

	protected open val trainingProportion = defaultTrainingProportion
	private val trainingNormalizedProportion get() = trainingProportion.coerceIn(0.0, 1.0)

	final override val trainingTopics: Set<String>

	final override val testTopics: Set<String>

	init {
		val split = split(corpus)
		trainingTopics = split.first
		testTopics = split.second
	}

	abstract fun sort(topics: Sequence<JudgedTopic<JudgedDocument>>): Sequence<JudgedTopic<JudgedDocument>>

	private fun split(corpus: Corpus): Pair<Set<String>, Set<String>> {
		val topics = corpus.trecTopics
		val redundantTopics = sort(topics).map { it.id }.toList()
		val globalTopics = redundantTopics
				.take((redundantTopics.size * globalNormalizedProportion).roundToInt())
		val trainingTopics = globalTopics
				.take((globalTopics.size * trainingNormalizedProportion).roundToInt())
				.toSet()
		val testTopics = globalTopics
				.take((globalTopics.size * (1.0 - trainingNormalizedProportion)).roundToInt())
				.toSet()
		return trainingTopics to testTopics
	}
}