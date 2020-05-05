package de.webis.webisstud.thesis.reimer

import de.webis.webisstud.thesis.reimer.correlation.rankCorrelations
import de.webis.webisstud.thesis.reimer.model.JudgedTopic
import de.webis.webisstud.thesis.reimer.model.TrecTask

fun main(vararg args: String) {
    TrecTaskRunner.run(
            args,
            defaultTask = TrecTask.Web2012,
            taskAction = TrecTask::rankCorrelations,
            topicAction = JudgedTopic<*>::rankCorrelations
    )
}
