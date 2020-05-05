package de.webis.webisstud.thesis.reimer.math

fun Iterable<Long>.standardDeviation(): Double {
    var n: Long = 0
    var mean = 0.0
    var m2 = 0.0

    for (x in this) {
        n += 1
        val delta = x - mean
        mean += delta / n
        m2 += delta * (x - mean)
    }

    return if (n < 2) Double.NaN else m2 / (n - 1)
}

fun Iterable<Long>.stats(): String {
    val list = toList()
    return "n: ${list.size}, μ: ${"%.2f".format(list.average())}, σ: ${"%.2f".format(list.standardDeviation())}, min: ${list.min()}, max: ${list.max()}"
}