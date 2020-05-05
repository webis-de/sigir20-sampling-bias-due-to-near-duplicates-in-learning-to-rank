package de.webis.webisstud.thesis.reimer.model.reader

import de.webis.webisstud.thesis.reimer.model.format.QrelLineFormat

object QrelReader : RelevanceReader by LineRelevanceReader(QrelLineFormat)
