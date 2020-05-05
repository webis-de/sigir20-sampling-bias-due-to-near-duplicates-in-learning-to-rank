package de.webis.webisstud.thesis.reimer.evaluation

import de.webis.webisstud.thesis.reimer.evaluation.internal.MetricEvaluation
import de.webis.webisstud.thesis.reimer.evaluation.internal.PerTopicEvaluation
import de.webis.webisstud.thesis.reimer.evaluation.internal.PerTopicNamedEvaluation
import de.webis.webisstud.thesis.reimer.evaluation.internal.SplitEvaluation
import de.webis.webisstud.thesis.reimer.ltr.JudgedRunLine
import de.webis.webisstud.thesis.reimer.ltr.MetricType
import de.webis.webisstud.thesis.reimer.ltr.split.Split
import de.webis.webisstud.thesis.reimer.model.Corpus

fun <T> RankingEvaluation<T>.onSplits(vararg splits: Split): RankingEvaluation<T> =
		SplitEvaluation(this, splits.toSet())

fun <T> RankingEvaluation<T>.perTopic(): RankingEvaluation<List<T>> =
		PerTopicEvaluation(this)

fun <T> RankingEvaluation<T>.perTopicNamed(): RankingEvaluation<Map<String, T>> =
		PerTopicNamedEvaluation(this)

fun MetricType.asEvaluation(): RankingEvaluation<Double> = MetricEvaluation(this)

fun <T> List<JudgedRunLine>.evaluateJson(
		evaluation: RankingEvaluation<T>,
		corpus: Corpus
) = evaluation.evaluateJson(this, corpus)

fun <T> List<JudgedRunLine>.evaluate(
		evaluation: RankingEvaluation<T>,
		corpus: Corpus
) = evaluation.evaluate(this, corpus)