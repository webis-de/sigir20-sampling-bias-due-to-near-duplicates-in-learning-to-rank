package de.webis.webisstud.thesis.reimer.model.reader

import de.webis.webisstud.thesis.reimer.model.format.PrelLineFormat

object PrelReader : RelevanceReader by LineRelevanceReader(PrelLineFormat)
