package de.webis.webisstud.thesis.reimer.model.reader

import de.webis.webisstud.thesis.reimer.model.format.FullrelLineFormat

object FullrelReader : RelevanceReader by LineRelevanceReader(FullrelLineFormat)
