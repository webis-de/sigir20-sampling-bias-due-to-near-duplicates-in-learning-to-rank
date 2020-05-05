package de.webis.webisstud.thesis.reimer.ltr.split

import de.webis.webisstud.thesis.reimer.groups.fingerprintGroups
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.JudgedDocument
import de.webis.webisstud.thesis.reimer.model.JudgedTopic

class MostRedundantTrainingTopicTrainTestSplit(corpus: Corpus) : GreedyTopicTrainTestSplit(corpus) {

	private val groups = corpus.fingerprintGroups

	override val id: String = "most-redundant-training"

	override fun sort(topics: Sequence<JudgedTopic<JudgedDocument>>): Sequence<JudgedTopic<JudgedDocument>> {
		return topics.sortedByDescending { topic ->
			val documents = topic.documents
			val redundantDocumentCount = documents.count { document ->
				groups.anyGroupContains(document.id)
			}
			redundantDocumentCount.toDouble() / documents.size
		}
	}
}