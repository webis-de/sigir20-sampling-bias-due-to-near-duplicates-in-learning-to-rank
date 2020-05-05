package de.webis.webisstud.thesis.reimer.math

fun indicator(value: Boolean): Int = if (value) 1 else 0
fun <T> indicator(element: T, collection: Iterable<T>): Int = indicator(element in collection)