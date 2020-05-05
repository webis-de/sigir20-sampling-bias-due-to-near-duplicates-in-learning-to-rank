package de.webis.webisstud.thesis.reimer.evaluation

import de.webis.webisstud.thesis.reimer.evaluation.internal.EvaluationJsonFields
import de.webis.webisstud.thesis.reimer.model.Corpus
import dev.reimer.serialization.jsonl.JsonL
import kotlinx.serialization.json.*
import java.io.File

fun main(args: Array<String>) {
	val json = Json(JsonConfiguration.Stable)
	val jsonL = JsonL(JsonConfiguration.Stable)

	val inputFile = File(args[0])
	val outputFile = File(args.getOrElse(1) { "${inputFile.parentFile.path}/${inputFile.nameWithoutExtension}-converted.jsonl" })
	val evaluations = json
			.parseJson(inputFile.readText())
			.jsonArray
	val newEvaluations = evaluations
			.asSequence()
			.map { it.jsonObject }
			.onEach { print('.') }
			.flatMap { evaluation ->
				val subEvaluations = mapOf(
						"run-size" to (evaluation.getValue("examplesInTrainingSet") to JsonNull),
						"first-wikipedia-rank" to (JsonNull to evaluation.getValue("firstWikipediaOccurences")),
						"first-irrelevant-wikipedia-rank" to (JsonNull to evaluation.getValue("firstIrrelevantWikipeidaOccurences")),
						"map" to (JsonNull to evaluation.getValue("map")),
						"map-per-topic" to (JsonNull to evaluation.getValue("map_all_topics")),
						"ndcg" to (evaluation.getValue("ndcg_train") to evaluation.getValue("ndcg")),
						"ndcg-per-topic" to (evaluation.getValue("ndcg_train_all_topics") to evaluation.getValue("ndcg_all_topics")),
						"ndcg@20" to (evaluation.getValue("ndcg_train_20") to evaluation.getValue("ndcg_20")),
						"ndcg@20-per-topic" to (evaluation.getValue("ndcg_train_20_all_topics") to evaluation.getValue("ndcg_20_all_topics"))
				)
				val baseConfiguration = mapOf(
						EvaluationJsonFields.corpus to JsonLiteral(Corpus.ClueWeb09.name),
						EvaluationJsonFields.trainTestSplit to evaluation.getValue("trainTestSplitStrategy"),
						EvaluationJsonFields.ranker to evaluation.getValue("algorithm"),
						EvaluationJsonFields.metric to evaluation.getValue("measure"),
						EvaluationJsonFields.undersampling to evaluation.getValue("redundancy"),
						EvaluationJsonFields.oversampling to evaluation.getValue("explicitOversampling"),
						EvaluationJsonFields.featureMutation to JsonLiteral("identity"),
						EvaluationJsonFields.runSampling to JsonLiteral("${evaluation.getPrimitive("deduplication").content}-and-${evaluation.getPrimitive("qrelConsistency").content}"),
						EvaluationJsonFields.trial to JsonLiteral("trial-0") // Already aggregated.
				)
				val trainEvaluations = subEvaluations
						.asSequence()
						.map { (name, pair) ->
							val (result, _) = pair
							JsonObject(baseConfiguration + mapOf(
									EvaluationJsonFields.trainSetResult to result,
									EvaluationJsonFields.evaluation to JsonLiteral(name)
							))
						}
				val testEvaluations = subEvaluations
						.asSequence()
						.map { (name, pair) ->
							val (_, result) = pair
							JsonObject(baseConfiguration + mapOf(
									EvaluationJsonFields.testSetResult to result,
									EvaluationJsonFields.evaluation to JsonLiteral(name)
							))
						}
				trainEvaluations + testEvaluations
			}
	jsonL.save(JsonElementSerializer, newEvaluations, outputFile)
}