package de.webis.webisstud.thesis.reimer.model

import java.time.LocalDateTime

enum class Corpus {
    ClueWeb09,
    ClueWeb12,
    Gov2,
    NewYorkTimes,
    WashingtonPost;

    val id = name.toLowerCase()

    companion object {
        fun find(name: String): Corpus? {
            val lowerCaseName = name.toLowerCase()
            return values().first { it.name.toLowerCase() == lowerCaseName }
        }

        fun valueOfCaseInsensitive(name: String): Corpus {
            return requireNotNull(find(name)) {
                "No value for name '$name'."
            }
        }

        val CLUE_WEB_12_ALEXA_DATE: LocalDateTime = LocalDateTime.of(
                2016,
                6,
                23,
                20,
                44,
                49
        )
    }
}