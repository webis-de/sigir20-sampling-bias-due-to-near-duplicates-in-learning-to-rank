package de.webis.webisstud.thesis.reimer.features.clueweb09

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class JsonTopicTrainTestSplit(
		@SerialName("name")
		val id: String,
		@SerialName("display-name")
		val name: String? = null,
		@SerialName("train")
		val trainingTopics: Set<String>,
		@SerialName("test")
		val testTopics: Set<String>
)
