package de.webis.webisstud.thesis.reimer.letor

import java.io.File

data class Partitions(
        val path: File
) {
    val test = path.resolve("test.txt")
    val train = path.resolve("train.txt")
    val validation = path.resolve("vali.txt")
}