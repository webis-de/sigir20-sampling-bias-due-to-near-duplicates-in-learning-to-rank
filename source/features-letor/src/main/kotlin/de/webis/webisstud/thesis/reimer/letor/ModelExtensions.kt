package de.webis.webisstud.thesis.reimer.letor

import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.TrecTask

val TrecTask.hasLetorQuerySet: Boolean
    get() {
        return when (this) {
            TrecTask.MillionQuery2007,
            TrecTask.MillionQuery2008 -> true
            else -> false
        }
    }

val TrecTask.letorQuerySet: QuerySet
    get() {
        return when (this) {
            TrecTask.MillionQuery2007 -> QuerySet.MillionQuery2007
            TrecTask.MillionQuery2008 -> QuerySet.MillionQuery2008
            else -> error("No Letor query set for this task.")
        }
    }

val Corpus.hasLetorQuerySet: Boolean
    get() {
        return when (this) {
            Corpus.ClueWeb09 -> true
            else -> false
        }
    }

val Corpus.letorQuerySet: QuerySet
    get() {
        return when (this) {
            Corpus.ClueWeb09 -> QuerySet.ClueWeb09
            else -> error("No Letor query set for this corpus.")
        }
    }