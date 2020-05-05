package de.webis.webisstud.thesis.reimer.model.reader

import de.webis.webisstud.thesis.reimer.model.Relevance
import java.io.InputStream

interface RelevanceReader {
    fun read(stream: InputStream): List<Relevance>
}