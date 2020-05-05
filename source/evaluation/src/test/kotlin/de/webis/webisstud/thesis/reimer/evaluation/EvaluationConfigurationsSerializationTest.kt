package de.webis.webisstud.thesis.reimer.evaluation

import org.junit.jupiter.api.Test
import java.io.ObjectOutputStream
import java.io.OutputStream

internal class EvaluationConfigurationsSerializationTest {

	@Test
	fun `Evaluation configurations can be serialized`() {
		serialize(EvaluationConfigurations.runSamplings)
		serialize(EvaluationConfigurations.rankingEvaluations)
	}

	private fun serialize(anything: Any) {
		ObjectOutputStream(OutputStream.nullOutputStream()).use { it.writeObject(anything) }
	}
}