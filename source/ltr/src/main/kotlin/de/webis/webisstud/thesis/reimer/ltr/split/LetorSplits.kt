package de.webis.webisstud.thesis.reimer.ltr.split

import de.webis.webisstud.thesis.reimer.letor.Partitions
import de.webis.webisstud.thesis.reimer.letor.hasLetorQuerySet
import de.webis.webisstud.thesis.reimer.letor.letorQuerySet
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.Document
import de.webis.webisstud.thesis.reimer.model.TrecTask
import de.webis.webisstud.thesis.reimer.model.format.FeatureVectorLineFormat
import de.webis.webisstud.thesis.reimer.model.trecTasks
import java.io.File
import java.io.Serializable

class LetorSplits(
		corpus: Corpus
) : AbstractSet<FeatureVectorSplitter>() {

	private class Splitter(
			override val id: String,
			partitions: Partitions
	) : FeatureVectorSplitter {

		val testMetadata = partitions.test.parseMetadata()
		val trainingMetadata = partitions.train.parseMetadata()
//		val validationMetadata = partitions.validation.parseMetadata()

		private fun File.parseMetadata(): Set<Document.Metadata> {
			return useLines { lines ->
				lines.map(FeatureVectorLineFormat::parse)
						.map(::EncapsulatedMetadata)
						.toSet()
			}
		}

		private data class EncapsulatedMetadata(
				override val documentId: String,
				override val topicId: String
		) : Document.Metadata, Serializable {
			constructor(vector: Document.Metadata) : this(vector.documentId, vector.topicId)
		}

		override fun getSplit(metadata: Document.Metadata): Split? {
			return when (EncapsulatedMetadata(metadata)) {
				in trainingMetadata -> Split.Training
				in testMetadata -> Split.Test
//				in validationMetadata -> Split.Validation
				else -> null
			}
		}

		override fun getSplits(metadata: Set<Document.Metadata>): Map<Document.Metadata, Split?> {
			val testMetadata = this.testMetadata intersect metadata
			val trainingMetadata = this.trainingMetadata intersect metadata
			val remainingMetadata = metadata - testMetadata - trainingMetadata
			return testMetadata.associateWith { Split.Test } +
					trainingMetadata.associateWith { Split.Training } +
					remainingMetadata.associateWith { null }
		}
	}

	private val splitters: Set<FeatureVectorSplitter> by lazy {
		val corpusSplit = if (corpus.hasLetorQuerySet) {
			sequenceOf(corpus.id to corpus.letorQuerySet)
		} else emptySequence()
		val querySets = corpusSplit + corpus
				.trecTasks
				.filter(TrecTask::hasLetorQuerySet)
				.map { task ->
					task.id to task.letorQuerySet
				}
		querySets.flatMap { (id, letorQuerySet) ->
			letorQuerySet.supervised.partitionFolds
					.asSequence()
					.map { partitions ->
						Splitter("${id}-${partitions.path.name.toLowerCase()}", partitions)
					}
		}.toSet()
	}

	override val size get() = splitters.size

	override fun iterator() = splitters.iterator()
}