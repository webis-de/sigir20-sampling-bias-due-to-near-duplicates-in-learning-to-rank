package de.webis.webisstud.thesis.reimer.evaluation

import de.webis.webisstud.thesis.reimer.data.Data
import de.webis.webisstud.thesis.reimer.evaluation.internal.*
import de.webis.webisstud.thesis.reimer.evaluation.internal.EvaluationJsonFields
import de.webis.webisstud.thesis.reimer.ltr.sampling.sample
import de.webis.webisstud.thesis.reimer.ltr.split.Split
import de.webis.webisstud.thesis.reimer.ltr.zipWithRelevance
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.format.RunLineFormat
import de.webis.webisstud.thesis.reimer.model.util.measure
import dev.reimer.kotlin.jvm.ktx.writeLinesTo
import dev.reimer.serialization.jsonl.JsonL
import dev.reimer.spark.ktx.*
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import kotlinx.serialization.map
import kotlinx.serialization.serializer
import mu.KotlinLogging
import java.io.File
import java.io.Writer
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.time.temporal.TemporalField
import org.apache.log4j.Level as Log4jLevel

private val json = Json(JsonConfiguration.Stable)
private val jsonL = JsonL(json)
private val evaluationsSerializer =
		(String.serializer() to JsonElement.serializer()).map
private const val rerankedTestFileName = "reranked"
private const val rerankedTrainingFileName = "reranked-training"

private val log = KotlinLogging.logger("RunEvaluation")

fun main(args: Array<String>) {
	val parser = ArgParser("runEvaluation")
	val timeString by parser.argument(ArgType.String)
	val debug by parser.option(ArgType.Boolean, fullName = "debug", shortName = "d").default(false)
	val useSpark by parser.option(ArgType.Boolean, fullName = "spark", shortName = "s").default(false)
	parser.parse(args)

	val (year, month, day, hour, minute) = timeString.split('-').map(String::toInt)
	val time = OffsetDateTime.of(year, month, day, hour, minute, 0, 0, ZoneOffset.UTC).toInstant()

	val evaluationConfigurations = EvaluationConfigurations(time, debug)
	evaluationConfigurations.evaluate(useSpark)
}

fun EvaluationConfigurations.evaluate(useSpark: Boolean) {
	log.info { "Saving to ${resultFile.absolutePath}" }

	configurations
			.filter { configuration ->
				val ready = configuration.isReady
				if (!ready) {
					log.warn { "Experiment $configuration is not yet ready." }
				}
				ready
			}
			.toList()
			.evaluate(useSpark, debug, resultFile)

	log.info { "All evaluations finished." }
}

private fun Collection<EvaluationConfiguration>.evaluate(
		useSpark: Boolean,
		debug: Boolean,
		resultFile: File
) {
	log.info {
		"Run $size evaluations${if (debug) " in debug mode" else ""} " +
				"(running with ${if (useSpark) "Spark" else "Kotlin Coroutines"})."
	}

	if (useSpark) {
		// Evaluate using Spark on a Hadoop cluster.
		setSparkLogLevel(Log4jLevel.INFO)
		spark {
			context {
				val tempDir = Data.newTempDir()
				log.info { "Collecting evaluations to temporary dir ${tempDir.absolutePath}." }
				val parts: List<File> = toRdd(this, size)
						.map(EvaluationConfiguration::evaluateExperiment)
						.coalesce(defaultParallelism)
						.mapPartitionsWithIndexSequence { partition, results ->
							val part = tempDir.resolve("$partition")
							results.asSequence().writeLinesTo(part)
							sequenceOf(part)
						}
						.collect()
				log.info { "Finished collecting evaluations to temporary dir. Start merging result files." }
				parts.mergeTo(resultFile)
				tempDir.delete()
			}
		}
	} else {
		// Evaluate using Kotlin Coroutines on the local machine.
		runBlocking(Dispatchers.Default) {
			val supervisor = SupervisorJob()
			with(CoroutineScope(coroutineContext + supervisor)) {
				map { configuration ->
					async {
						configuration.evaluateExperiment()
					}
				}.awaitAll().asSequence().writeLinesTo(resultFile)
			}
		}
	}
}

private fun File.parseRun() = useLines { lines -> lines.map(RunLineFormat::parse).toList() }
private val File.rerankedTestRunFile get() = resolve(rerankedTestFileName)
private val File.rerankedTrainingRunFile get() = resolve(rerankedTrainingFileName)
private val EvaluationConfiguration.isReady
	get() = experimentDirs.rerankedTestRunFile.exists() && experimentDirs.rerankedTrainingRunFile.exists()

private fun EvaluationConfiguration.evaluateExperiment(): String {
	log.info { "Start $this" }
	val result = mapOf(
			EvaluationJsonFields.corpus to JsonLiteral(corpus),
			EvaluationJsonFields.trainTestSplit to JsonLiteral(splitter),
			EvaluationJsonFields.ranker to JsonLiteral(ranker),
			EvaluationJsonFields.metric to JsonLiteral(metric),
			EvaluationJsonFields.undersampling to JsonLiteral(undersampling),
			EvaluationJsonFields.oversampling to JsonLiteral(oversampling),
			EvaluationJsonFields.featureMutation to JsonLiteral(featureMutation),
			EvaluationJsonFields.trial to JsonLiteral(trial),
			EvaluationJsonFields.runSampling to JsonLiteral(runSampling.id),
			EvaluationJsonFields.evaluation to JsonLiteral(evaluation.id),
			EvaluationJsonFields.testSetResult to evaluateRun(Split.Test),
			EvaluationJsonFields.trainSetResult to evaluateRun(Split.Training)
	)
	log.info { "Finish $this" }
	return json.stringify(evaluationsSerializer, result)
}

private fun EvaluationConfiguration.evaluateRun(split: Split): JsonElement {
	// Skip evaluation if it should not be computed on this split
	if (split !in evaluation.splits) return JsonNull
	return measure("evaluating ${split.name.toLowerCase()} run") {
		val corpus = Corpus.valueOfCaseInsensitive(corpus)

		experimentDirs
				.run {
					when (split) {
						Split.Test -> rerankedTestRunFile
						Split.Training -> rerankedTrainingRunFile
					}
				}
				.parseRun()
				.zipWithRelevance(corpus)
				.sample(runSampling, corpus)
				.evaluateJson(evaluation, corpus)
	}
}
