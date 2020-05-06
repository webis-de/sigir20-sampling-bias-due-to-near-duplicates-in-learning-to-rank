package de.webis.webisstud.thesis.reimer.features.clueweb09

import de.webis.webisstud.thesis.reimer.data.Data
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.FeatureVector
import de.webis.webisstud.thesis.reimer.model.Resource
import de.webis.webisstud.thesis.reimer.model.format.FeatureVectorLineFormat
import de.webis.webisstud.thesis.reimer.model.trecTasks
import dev.reimer.kotlin.jvm.ktx.writeLinesTo
import dev.reimer.serialization.jsonl.JsonL

private val BASE_DIR = Data.featuresDir.resolve("ClueWeb09").apply { mkdirs() }
private val NULL_FILE = BASE_DIR.resolve("NULL.txt")
private val MIN_FILE = BASE_DIR.resolve("min.txt")
private val NORM_FILE = BASE_DIR.resolve("Querylevelnorm.txt")

fun main() {
	FeatureVectorLineFormat.setOption(FeatureVectorLineFormat.Option.FloatRelevance, false)
	FeatureVectorLineFormat.setOption(FeatureVectorLineFormat.Option.FixedPrecision, true)
	generateNullFeatures()
	nullToMinFeatures()
	minToNormFeatures()
	copyPerTrecTask()
	copySplits()
}

private fun copyPerTrecTask() {
	val documentTaskLookup = Corpus.ClueWeb09
			.trecTasks
			.flatMap { task ->
				val taskDir = Data.featuresDir.resolve(task.name)
				task.topics
						.asSequence()
						.flatMap { topic ->
							topic.documents.asSequence()
						}
						.map { document ->
							document.id to taskDir
						}
			}
			.toMap()

	// Clear task folders.
	for (dir in documentTaskLookup.values.toSet()) {
		dir.delete()
		dir.mkdirs()
	}

	for (file in listOf(NULL_FILE, MIN_FILE, NORM_FILE)) {
		file.useLines { lines ->
			lines.map(FeatureVectorLineFormat::parse)
					.groupBy { documentTaskLookup.getValue(it.documentId).resolve(file.name) }
					.forEach { (taskFile, vectors) ->
						println("Copy from ${file.absolutePath} to ${taskFile.absolutePath}.")
						vectors.asSequence()
								.map(FeatureVectorLineFormat::format)
								.writeLinesTo(taskFile)
					}
		}
	}
}

private fun generateNullFeatures() {
	println("Generating null features at ${NULL_FILE.absolutePath}.")
	ClueWeb09FeatureVectors
			.asSequence()
			.map(FeatureVectorLineFormat::format)
			.writeLinesTo(NULL_FILE)
}

private fun nullToMinFeatures() {
	println("Convert null features at ${NULL_FILE.absolutePath} to min features at ${MIN_FILE.absolutePath}.")
	NULL_FILE.useLines { lines ->
		lines.map(FeatureVectorLineFormat::parse)
				.groupBy(FeatureVector::topicId)
				.asSequence()
				.flatMap { (_, vectors) ->
					val indices = vectors.first().features.indices
					val minimums = indices.map { index ->
						vectors.asSequence()
								.map { vector -> vector.features[index] }
								.filterNot(Float::isNaN)
								.min() ?: 0f
					}
					vectors.asSequence()
							.map { vector ->
								val features = vector.features
										.mapIndexed { index, feature ->
											if (feature.isNaN()) {
												minimums[index]
											} else feature
										}
								vector.copy(features = features)
							}
				}
				.map(FeatureVectorLineFormat::format)
				.writeLinesTo(MIN_FILE)
	}
}

private fun minToNormFeatures() {
	println("Convert min features at ${MIN_FILE.absolutePath} to norm features at ${NORM_FILE.absolutePath}.")
	MIN_FILE.useLines { lines ->
		lines.map(FeatureVectorLineFormat::parse)
				.groupBy(FeatureVector::topicId)
				.asSequence()
				.flatMap { (_, vectors) ->
					val indices = vectors.first().features.indices
					val minimums = indices.map { index ->
						vectors.asSequence()
								.map { vector -> vector.features[index] }
								.min()!!
					}
					val maximums = indices.map { index ->
						vectors.asSequence()
								.map { vector -> vector.features[index] }
								.max()!!
					}
					vectors.asSequence()
							.map { vector ->
								val features = vector.features
										.mapIndexed { index, feature ->
											val min = minimums[index]
											val max = maximums[index]
											if (min != max) {
												(feature - min) / (max - min)
											} else 0.5f
										}
								vector.copy(features = features)
							}
				}
				.map(FeatureVectorLineFormat::format)
				.writeLinesTo(NORM_FILE)
	}
}

private val SPLITS_RESOURCE = Resource("/clueweb09/train-test-splits.jsonl", JsonTopicTrainTestSplit::class)
private const val CROSS_VALIDATION_PREFIX = "5-fold-cross-validation-"
fun copySplits() {
	val splits = SPLITS_RESOURCE.parseTopicTrainTestSplits()
	val normVectors = NORM_FILE.useLines { lines ->
		lines.map(FeatureVectorLineFormat::parse).toList()
	}
	splits.forEach { split ->
		val dirName = if (split.id.startsWith(CROSS_VALIDATION_PREFIX)) {
			"Fold" + split.id.removePrefix(CROSS_VALIDATION_PREFIX)
		} else {
			split.id.splitToSequence('-')
					.joinToString(separator = "", transform = String::capitalize)
		}
		val splitDir = BASE_DIR.resolve(dirName).apply { mkdir() }
		val trainFile = splitDir.resolve("train.txt")
		normVectors.asSequence()
				.filter { it.topicId in split.trainingTopics }
				.map { FeatureVectorLineFormat.format(it) }
				.writeLinesTo(trainFile)
		val testFile = splitDir.resolve("test.txt")
		normVectors.asSequence()
				.filter { it.topicId in split.testTopics }
				.map { FeatureVectorLineFormat.format(it) }
				.writeLinesTo(testFile)
	}
}

private val JSON_L = JsonL()
private fun Resource.parseTopicTrainTestSplits(): Set<JsonTopicTrainTestSplit> {
	return inputStream()
			.reader()
			.useLines { lines ->
				JSON_L.parse(JsonTopicTrainTestSplit.serializer(), lines).toSet()
			}
}
