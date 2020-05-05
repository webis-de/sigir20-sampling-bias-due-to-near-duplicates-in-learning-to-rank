package de.webis.webisstud.thesis.reimer.ltr

import ciir.umass.edu.learning.DataPoint
import de.webis.webisstud.thesis.reimer.ltr.files.source.featureVectorSource
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.FeatureVector
import de.webis.webisstud.thesis.reimer.model.Relevance
import de.webis.webisstud.thesis.reimer.model.RunLine
import de.webis.webisstud.thesis.reimer.model.util.measure
import de.webis.webisstud.thesis.reimer.model.util.toRelevance

internal fun FeatureVector.asDataPoint() = FeatureVectorDataPoint(this)

internal fun DataPoint.documentBm25(corpus: Corpus): Float {
	return when (corpus) {
		Corpus.ClueWeb09 -> getFeatureValue(15)
		Corpus.Gov2 -> getFeatureValue(25)
		else -> error("Unsupported corpus $corpus.")
	}
}

fun FeatureVector.documentBm25(corpus: Corpus) = asDataPoint().documentBm25(corpus)

internal fun JudgedRunLine.asDataPoint() = RunDataPoint(this)

fun List<RunLine>.zipWithRelevance(corpus: Corpus): List<JudgedRunLine> {
	return measure("zipping with relevance") {
		fun sameDocument(relevance: Relevance, runLine: RunLine): Boolean {
			return relevance.documentId == runLine.documentId && relevance.topicId == runLine.topicId
		}

		val judgements = corpus
				// Taking judgements from Qrels won't work.
				// .trecDocuments
				// .map(JudgedDocument::relevance)
				// As in LETOR features, some documents are judged
				// that have no judgements in Qrels,
				// we must use judgements from features here.
				.featureVectorSource
				.map(FeatureVector::toRelevance)
				.filter { relevance ->
					any { runLine ->
						sameDocument(relevance, runLine)
					}
				}
				.toList()

		map { runLine ->
			JudgedRunLine(
					runLine,
					judgements.first { relevance -> sameDocument(relevance, runLine) }
			)
		}
	}
}