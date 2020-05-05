package de.webis.webisstud.thesis.reimer.letor

import java.io.File

data class Partitions(
        val test: File,
        val train: File,
        val validation: File
) {
    constructor(path: File) : this(
            path.resolve("test.txt"),
            path.resolve("train.txt"),
            path.resolve("vali.txt")
    )
}