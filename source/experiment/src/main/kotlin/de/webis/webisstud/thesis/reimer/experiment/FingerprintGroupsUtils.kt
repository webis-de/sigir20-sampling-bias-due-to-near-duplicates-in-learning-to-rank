package de.webis.webisstud.thesis.reimer.experiment

import de.webis.webisstud.thesis.reimer.corpus.url.urls
import de.webis.webisstud.thesis.reimer.groups.canonical.CanonicalFingerprintGroups
import de.webis.webisstud.thesis.reimer.groups.canonical.asCanonicalFingerprintGroups
import de.webis.webisstud.thesis.reimer.groups.canonical.canonicalFingerprintGroups
import de.webis.webisstud.thesis.reimer.model.Corpus
import dev.reimer.domain.ktx.domainOrNull

val Corpus.canonicalFingerprintGroupsContainsWikipedia: CanonicalFingerprintGroups
	get() {
		val groups = canonicalFingerprintGroups.groups
		val ids: Set<String> = groups.flatMapTo(mutableSetOf()) { it.ids }
		val urls = urls.filter { (id, _) -> id in ids }
		return groups.filterTo(mutableSetOf()) { group ->
			group.ids.any { urls[it]?.domainOrNull?.root == "wikipedia.org" }
		}.asCanonicalFingerprintGroups()
	}