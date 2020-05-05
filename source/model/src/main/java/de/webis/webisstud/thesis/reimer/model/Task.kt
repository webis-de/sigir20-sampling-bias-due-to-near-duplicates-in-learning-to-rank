package de.webis.webisstud.thesis.reimer.model

interface Task {
    val id: String
    val corpus: Corpus
    val title: String? get() = null
    val description: String? get() = null
    val url: String? get() = null
}