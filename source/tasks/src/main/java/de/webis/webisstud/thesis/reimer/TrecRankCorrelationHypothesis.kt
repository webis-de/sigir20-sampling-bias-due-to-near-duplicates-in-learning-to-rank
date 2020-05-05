package de.webis.webisstud.thesis.reimer

import de.webis.webisstud.thesis.reimer.correlation.rankCorrelationHypothesis
import de.webis.webisstud.thesis.reimer.model.TrecTask

fun main(vararg args: String) {
    TrecTaskRunner.run(
            args,
            defaultTask = TrecTask.Web2012,
            taskAction = TrecTask::rankCorrelationHypothesis
    )
}
