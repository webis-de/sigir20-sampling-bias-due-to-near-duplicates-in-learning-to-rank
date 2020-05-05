package de.webis.webisstud.thesis.reimer

import de.webis.webisstud.thesis.reimer.model.JudgedTopic
import de.webis.webisstud.thesis.reimer.model.TrecTask

object TrecTaskRunner {
    fun run(args: Array<out String>, defaultTask: TrecTask? = null, taskAction: (TrecTask) -> Any?, topicAction: ((JudgedTopic<*>) -> Any?)? = null) {
        val id = args.getOrElse(0) {
            defaultTask?.id
        }
        requireNotNull(id) {
            "No task specified."
        }
        val task = TrecTask.find(id)
        requireNotNull(task) {
            "TREC task not found."
        }
        if (topicAction != null) {
            val topic = args.getOrNull(1)
            if (topic != null) {
                topicAction(task.getTopic(topic))
            } else {
                taskAction(task)
            }
        } else {
            taskAction(task)
        }
    }
}
