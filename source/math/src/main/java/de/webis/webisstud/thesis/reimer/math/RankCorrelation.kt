package de.webis.webisstud.thesis.reimer.math

infix fun <T : Comparable<T>> List<T>.kendallTau(other: List<T>) = intersectionKendallTauCorrelation(other)

fun <T : Comparable<T>> List<T>.kendallTauAtK(b: List<T>, k: Int): Double {
    val limited = take(k)
    val otherLimited = b.take(k)
    val expanded = limited.expandWith(otherLimited)
    val otherExpanded = otherLimited.expandWith(limited)
    return expanded.kendallTau(otherExpanded)
}

private fun <T : Comparable<T>> List<T>.expandWith(other: List<T>): List<T> {
    val ret = toMutableList()
    val tmp = other.toMutableList()
    tmp.removeAll(ret)
    ret.addAll(tmp)
    return ret
}
