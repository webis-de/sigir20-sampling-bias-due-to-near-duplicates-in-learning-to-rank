package de.webis.webisstud.thesis.reimer.model.format

interface Converter<F, T> {
    fun parse(from: F): T
    fun format(from: T): F
}
