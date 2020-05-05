package de.webis.webisstud.thesis.reimer.ltr.util

import java.util.logging.Handler
import java.util.logging.LogRecord

object SimpleConsoleHandler : Handler() {

    override fun publish(record: LogRecord) {
        println(record.message)
    }

    override fun flush() {}

    override fun close() {}
}
