package de.webis.webisstud.thesis.reimer.ltr.split

import de.webis.webisstud.thesis.reimer.model.Document

abstract class TopicTrainTestSplit : FeatureVectorSplitter {
	abstract val trainingTopics: Set<String>
	abstract val testTopics: Set<String>

	final override fun getSplit(metadata: Document.Metadata): Split? {
		return when (metadata.topicId) {
			in trainingTopics -> Split.Training
			in testTopics -> Split.Test
			else -> null
		}
	}
}