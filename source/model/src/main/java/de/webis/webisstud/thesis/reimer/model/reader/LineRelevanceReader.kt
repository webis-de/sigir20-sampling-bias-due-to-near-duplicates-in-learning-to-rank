package de.webis.webisstud.thesis.reimer.model.reader

import de.webis.webisstud.thesis.reimer.model.Relevance
import de.webis.webisstud.thesis.reimer.model.format.StringFormat
import java.io.InputStream

class LineRelevanceReader(
        private val lineFormat: StringFormat<Relevance>
) : RelevanceReader {

    override fun read(stream: InputStream): List<Relevance> {
        return stream
                .bufferedReader()
                .useLines { lines ->
                    lines.map(String::trim)
                            .map(lineFormat::parse)
                            .toList()
                }
    }
}