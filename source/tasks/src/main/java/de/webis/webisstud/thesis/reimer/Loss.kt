package de.webis.webisstud.thesis.reimer

data class Loss(
        val pointwise: Float,
        val pairwise: Float,
        val listwise: Float
)