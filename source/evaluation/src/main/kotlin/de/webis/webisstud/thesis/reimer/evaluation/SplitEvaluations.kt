package de.webis.webisstud.thesis.reimer.evaluation

import de.webis.webisstud.thesis.reimer.evaluation.internal.EvaluationJsonFields
import dev.reimer.kotlin.jvm.ktx.writeLinesTo
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonObject
import java.io.File
import java.time.OffsetDateTime
import java.time.ZoneOffset

fun main(args: Array<String>) {
	val parser = ArgParser("summarize-evaluation")
	val filePathOrTime by parser.argument(ArgType.String)
	parser.parse(args)

	val evaluationsFile = File(filePathOrTime)
			.takeIf(File::exists)

	if (evaluationsFile != null) return splitEvaluations(evaluationsFile)

	val (year, month, day, hour, minute) = filePathOrTime.split('-').map(String::toInt)
	val time = OffsetDateTime.of(year, month, day, hour, minute, 0, 0, ZoneOffset.UTC).toInstant()
	return splitEvaluations(EvaluationConfigurations(time).resultFile)
}

fun splitEvaluations(evaluationFile: File) {
	val types = evaluationFile.useJsonLines { lines ->
		lines.map { line ->
			line.getPrimitive(EvaluationJsonFields.evaluation).content
		}.toSet()
	}

	for (type in types) {
		println("Splitting evaluation results for '$type'.")

		val file = evaluationFile.resolveSibling(
				"${evaluationFile.nameWithoutExtension}-$type.${evaluationFile.extension}"
		)

		evaluationFile.useJsonLines { lines ->
			lines.filter { line ->
				line.getPrimitive(EvaluationJsonFields.evaluation).content == type
			}.map(JsonObject::toString).writeLinesTo(file)
		}
	}

	println("Finished splitting evaluation results.")
}

fun <R> File.useJsonLines(block: (Sequence<JsonObject>) -> R): R {
	val json = Json(JsonConfiguration.Stable)
	return useLines { lines ->
		val rows = lines.map { line ->
			json.parseJson(line).jsonObject
		}
		block(rows)
	}
}