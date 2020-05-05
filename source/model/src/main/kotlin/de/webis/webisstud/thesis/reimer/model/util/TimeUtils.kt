package de.webis.webisstud.thesis.reimer.model.util

import java.time.LocalTime
import java.time.temporal.ChronoUnit

inline fun <T> measure(name: String, block: () -> T): T {
	println("Start $name at ${LocalTime.now()}.")
	val start: LocalTime = LocalTime.now()
	val result = block()
	val end: LocalTime = LocalTime.now()
	val duration = start.until(end, ChronoUnit.MILLIS)
	println("Finish $name at ${LocalTime.now()}, took ${duration}ms.")
	return result
}