package de.webis.webisstud.thesis.reimer.evaluation

import de.webis.webisstud.thesis.reimer.ltr.JudgedRunLine
import de.webis.webisstud.thesis.reimer.ltr.split.Split
import de.webis.webisstud.thesis.reimer.model.Corpus
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.Serializable

/**
 * Evaluation on a ranked run list, commonly parsed from a run file.
 */
interface RankingEvaluation<T> : Serializable {
    /**
     * Unique ID for the evaluation, used for output file name.
     */
    val id: String

    val serializer: KSerializer<T>

    val splits: Set<Split> get() = Split.values().toSet()

    /**
     * Run the evaluation.
     */
    fun evaluate(run: List<JudgedRunLine>, corpus: Corpus): T

    fun evaluateJson(run: List<JudgedRunLine>, corpus: Corpus) =
            json.toJson(serializer, evaluate(run, corpus))

    private companion object {
        private val json = Json(JsonConfiguration.Stable)
    }
}
