package de.webis.webisstud.thesis.reimer.ltr.files.run

import de.webis.webisstud.thesis.reimer.ltr.files.source.MetadataSource
import de.webis.webisstud.thesis.reimer.model.FeatureVector
import de.webis.webisstud.thesis.reimer.model.RunLine
import de.webis.webisstud.thesis.reimer.model.format.RunLineFormat

class RunSource(
		private val source: MetadataSource<FeatureVector>
) : MetadataSource<RunLine> {

	override val format = RunLineFormat

	private val elements by lazy {
		println("Load runs <- features") // TODO
		source
				.groupBy { it.topicId }
				.asSequence()
				.flatMap { (_, vectors) ->
					val size = vectors.size
                    vectors.asSequence()
                            .mapIndexed { index, vector ->
	                            RunLine(vector.documentId, vector.topicId, (size - index).toFloat(), index + 1)
                            }
                }
    }

    override fun iterator() = elements.iterator()
}
