package de.webis.webisstud.thesis.reimer.math

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class StandardDeviationTest {

    @Test
    fun standardDeviation() {
        val list = listOf<Long>(10, 12, 23, 23, 16, 23, 21, 16)
        val standardDeviation = list.standardDeviation()
        assertEquals(4.8989794855664, standardDeviation, 0.0001)
    }
}