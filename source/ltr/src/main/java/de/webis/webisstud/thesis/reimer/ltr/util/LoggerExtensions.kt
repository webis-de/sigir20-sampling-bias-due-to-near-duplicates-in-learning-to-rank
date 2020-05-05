package de.webis.webisstud.thesis.reimer.ltr.util

import java.util.logging.Handler
import java.util.logging.LogManager
import java.util.logging.Logger

fun LogManager.setHandler(handler: Handler) {
    for (logger in loggers) {
        logger.setHandler(handler)
    }
}

val LogManager.loggers: Sequence<Logger>
    get() = loggerNames.asSequence().map { getLogger(it) }

fun Logger.setHandler(handler: Handler) {
    val hasHandlers = handlers.isNotEmpty()
    clearHandlers()
    if (hasHandlers) {
        addHandler(handler)
    }
}

fun Logger.clearHandlers() {
    for (handler: Handler in handlers) {
        removeHandler(handler)
    }
}
