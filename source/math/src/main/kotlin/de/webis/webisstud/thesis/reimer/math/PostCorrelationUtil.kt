package de.webis.webisstud.thesis.reimer.math

import org.apache.commons.math3.stat.correlation.KendallsCorrelation
import org.apache.commons.math3.util.Precision.equals

private val kendalls = KendallsCorrelation()

internal fun <T : Comparable<T>> List<T>.intersectionKendallTauCorrelation(other: List<T>): Double {
    return calculateCorrelation(this, other) { xArray, yArray -> kendalls.correlation(xArray, yArray) }
}

private fun <T : Comparable<T>> calculateCorrelation(firstRanking: List<T>, secondRanking: List<T>, correlation: (DoubleArray, DoubleArray) -> Double): Double {
    val firstRankingArray = buildCorrelationArray(firstRanking, secondRanking)
    val secondRankingArray = buildCorrelationArray(secondRanking, firstRanking)
    return when {
        firstRankingArray.isEmpty() -> 0.0
        firstRankingArray.size == 1 -> {
            when {
                equals(firstRankingArray[0], secondRankingArray[0]) -> 1.0
                else -> 0.0
            }
        }
        else -> correlation(firstRankingArray, secondRankingArray)
    }
}

private fun <T : Comparable<T>> buildCorrelationArray(firstRanking: List<T>, secondRanking: List<T>): DoubleArray {
    val firstRankingIds = firstRanking
            .checkHasNoDuplicates()
            .toMutableList()
            .apply {
                retainAll(secondRanking.checkHasNoDuplicates())
            }
    val indexValues = firstRankingIds.toList()
    return indexValues
            .sorted()
            .map { firstRankingIds.indexOf(it) }
            .map(Int::toDouble)
            .toDoubleArray()
}

private fun <T> List<T>.checkHasNoDuplicates(): List<T> {
    check(distinct().size == size) { "The list contains duplicates." }
    return this
}
