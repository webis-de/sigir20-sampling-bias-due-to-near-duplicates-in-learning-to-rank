package de.webis.webisstud.thesis.reimer.model.format

/**
 * Returns a substring before the first occurrence of [delimiter].
 * If the string does not contain the delimiter, returns [missingDelimiterValue] which defaults to the original string.
 */
fun CharSequence.substringBefore(delimiter: Regex, missingDelimiterValue: CharSequence = this): String {
    val match = delimiter.find(this)
    return if (match == null) missingDelimiterValue.toString() else substring(0, match.range.first)
}

/**
 * Returns a substring after the first occurrence of [delimiter].
 * If the string does not contain the delimiter, returns [missingDelimiterValue] which defaults to the original string.
 */
fun CharSequence.substringAfter(delimiter: Regex, missingDelimiterValue: CharSequence = this): String {
    val match = delimiter.find(this)
    return if (match == null) missingDelimiterValue.toString() else substring(match.range.last + 1, length)
}

/**
 * Returns a substring before the last occurrence of [delimiter].
 * If the string does not contain the delimiter, returns [missingDelimiterValue] which defaults to the original string.
 */
fun CharSequence.substringBeforeLast(delimiter: Regex, missingDelimiterValue: CharSequence = this): String {
    val match = delimiter.findAll(this).lastOrNull()
    return if (match == null) missingDelimiterValue.toString() else substring(0, match.range.first)
}

/**
 * Returns a substring after the last occurrence of [delimiter].
 * If the string does not contain the delimiter, returns [missingDelimiterValue] which defaults to the original string.
 */
fun CharSequence.substringAfterLast(delimiter: Regex, missingDelimiterValue: CharSequence = this): String {
    val match = delimiter.findAll(this).lastOrNull()
    return if (match == null) missingDelimiterValue.toString() else substring(match.range.last + 1, length)
}