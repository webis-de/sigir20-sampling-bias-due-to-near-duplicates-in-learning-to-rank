package de.webis.webisstud.thesis.reimer.experiment

import de.webis.webisstud.thesis.reimer.ltr.RankerType
import de.webis.webisstud.thesis.reimer.ltr.split.featureVectorSplitters
import de.webis.webisstud.thesis.reimer.model.Corpus
import org.junit.jupiter.api.Test
import java.io.ObjectOutputStream
import java.io.OutputStream

internal class ExperimentConfigurationsSerializationTest {

	private companion object {
		private val corpora = arrayOf(
				Corpus.ClueWeb09,
				Corpus.Gov2
		)
	}

	@Test
	fun `Experiments configurations can be serialized`() {
		serialize(ExperimentConfigurations.rankers)
		serialize(ExperimentConfigurations.metrics)
		serialize(ExperimentConfigurations.undersamplings)
		serialize(ExperimentConfigurations.oversamplings)
		serialize(ExperimentConfigurations.featureMutations)
		for (corpus in corpora) {
			serialize(corpus.featureVectorSplitters)
			serialize(RankerType.Bm25(corpus))
		}
	}

	private fun serialize(anything: Any) {
		ObjectOutputStream(OutputStream.nullOutputStream()).use { it.writeObject(anything) }
	}
}