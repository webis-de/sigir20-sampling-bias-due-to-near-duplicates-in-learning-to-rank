package de.webis.webisstud.thesis.reimer.evaluation.internal

import dev.reimer.domain.ktx.domainOrNull
import java.net.URL

internal fun Map<String, URL>.getDomainRootOrHostname(documentId: String) =
		get(documentId)?.let { it.domainOrNull?.root ?: it.host }

internal fun Map<String, URL>.isWikipedia(documentId: String) =
		getDomainRootOrHostname(documentId) == "wikipedia.org"