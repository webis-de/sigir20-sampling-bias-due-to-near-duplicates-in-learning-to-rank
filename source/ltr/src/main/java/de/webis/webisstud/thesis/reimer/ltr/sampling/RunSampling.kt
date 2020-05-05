package de.webis.webisstud.thesis.reimer.ltr.sampling

import de.webis.webisstud.thesis.reimer.ltr.JudgedRunLine
import de.webis.webisstud.thesis.reimer.model.Corpus
import java.io.Serializable

/**
 * Sampling strategy for removing runs from evaluation or modifying their judgements.
 */
interface RunSampling : Serializable {
	val id: String

	fun sample(items: List<JudgedRunLine>, corpus: Corpus): List<JudgedRunLine>
}