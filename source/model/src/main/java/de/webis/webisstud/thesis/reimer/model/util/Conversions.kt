package de.webis.webisstud.thesis.reimer.model.util

import de.webis.webisstud.thesis.reimer.model.FeatureVector
import de.webis.webisstud.thesis.reimer.model.Relevance
import kotlin.math.floor

private fun Float.checkIsInt() = check(floor(this) == this) { "Float $this is not Int." }

private fun Float.toIntChecked() = apply { checkIsInt() }.toInt()

fun FeatureVector.toRelevance() = Relevance(documentId, topicId, relevance.toIntChecked())
