package de.webis.webisstud.thesis.reimer.experiment

import de.webis.webisstud.thesis.reimer.ltr.documentBm25
import de.webis.webisstud.thesis.reimer.ltr.files.source.featureVectorSource
import de.webis.webisstud.thesis.reimer.ltr.pipeline.TrainedReranker
import de.webis.webisstud.thesis.reimer.ltr.sampling.mutate
import de.webis.webisstud.thesis.reimer.ltr.sampling.plus
import de.webis.webisstud.thesis.reimer.ltr.sampling.sample
import de.webis.webisstud.thesis.reimer.ltr.split.Split
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.format.FeatureVectorLineFormat
import dev.reimer.kotlin.jvm.ktx.useTempFile
import dev.reimer.kotlin.jvm.ktx.writeLinesTo
import dev.reimer.spark.ktx.context
import dev.reimer.spark.ktx.setSparkLogLevel
import dev.reimer.spark.ktx.spark
import dev.reimer.spark.ktx.toRdd
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.apache.log4j.Level

private val log = KotlinLogging.logger("RunExperiment")

fun main(args: Array<String>) {
	val parser = ArgParser("runExperiment")
	val corpusString by parser.argument(ArgType.String)
	val debug by parser.option(ArgType.Boolean, fullName = "debug", shortName = "d").default(false)
	val useSpark by parser.option(ArgType.Boolean, fullName = "spark", shortName = "s").default(false)
	parser.parse(args)

	val corpus = requireNotNull(Corpus.find(corpusString)) { "No corpus given." }
	ExperimentConfigurations(corpus, debug)
			.configurations
			.runExperiments(useSpark, debug)
	log.info { "All experiments finished." }
}

private fun Set<ExperimentConfiguration>.runExperiments(useSpark: Boolean, debug: Boolean) {
	log.info {
		"Run $size experiments${if (debug) " in debug mode" else ""} " +
				"(running with ${if (useSpark) "Spark" else "Kotlin Coroutines"})."
	}

	if (useSpark) {
		// Run experiments using Spark on a Hadoop cluster.
		setSparkLogLevel(Level.INFO)
		spark {
			context {
				toRdd(this, size)
						.foreach { configuration ->
							configuration.runExperiment(debug)
						}
			}
		}
	} else {
		// Run experiments using Kotlin Coroutines on the local machine.
		runBlocking {
			val supervisor = SupervisorJob()
			with(CoroutineScope(coroutineContext + supervisor)) {
				map { configuration ->
					launch {
						configuration.runExperiment(debug)
					}
				}.joinAll()
			}
		}
	}
}

private fun ExperimentConfiguration.runExperiment(debug: Boolean) {
	log.info { "Start $this" }
	val reranker = train(debug)
	rerank(reranker)
	log.info { "Finish $this" }
}

private fun ExperimentConfiguration.train(debug: Boolean): TrainedReranker {
	val vectors = corpus
			.featureVectorSource
			.filter { splitter.getSplit(it) == Split.Training }
			.filter { it.documentBm25(corpus) > 0f }
			.toList()
			.sample(underSampling + overSampling, Split.Training, corpus)
			.mutate(featureMutation, Split.Training, corpus)
			.toList()

	val reranker = useTempFile { trainFile ->
		vectors.asSequence()
				.map(FeatureVectorLineFormat::format)
				.writeLinesTo(trainFile)
		TrainedReranker(trainFile, ranker, metric).also(TrainedReranker::train)
	}

	// Save trained model.
	if (debug) {
		val modelFile = experimentDir.resolve("model")
		reranker.save(modelFile)
	}

	// Save reranked training vectors.
	reranker.rerankFeatureVectors(vectors.asSequence())
			.writeLinesTo(experimentDir.resolve("reranked-training"))

	return reranker
}

private fun ExperimentConfiguration.rerank(reranker: TrainedReranker) {
	val vectors = corpus
			.featureVectorSource
			.filter { splitter.getSplit(it) == Split.Test }
			.toList()
			.sample(underSampling + overSampling, Split.Test, corpus)
			.mutate(featureMutation, Split.Test, corpus)

	// Save reranked test vectors.
	reranker.rerankFeatureVectors(vectors.asSequence())
			.writeLinesTo(experimentDir.resolve("reranked"))
}