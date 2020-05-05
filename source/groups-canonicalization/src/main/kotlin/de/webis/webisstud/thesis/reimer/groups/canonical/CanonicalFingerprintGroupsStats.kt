package de.webis.webisstud.thesis.reimer.groups.canonical

import de.webis.webisstud.thesis.reimer.corpus.url.urls
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.trecDocuments
import de.webis.webisstud.thesis.reimer.model.trecTopics
import dev.reimer.domain.ktx.domainOrNull
import dev.reimer.kotlin.jvm.ktx.inverse
import dev.reimer.kotlin.jvm.ktx.mapToMap

fun main(args: Array<String>) {
    val corpus = Corpus.valueOfCaseInsensitive(args.first())
	val groups = corpus.canonicalFingerprintGroups.groups
	val consistentGroups: Set<CanonicalFingerprintGroup> = groups.filterTo(mutableSetOf()) { group ->
		group.isConsistentCanonical
	}
	println("Consistent groups: ${consistentGroups.size.toDouble() / groups.size}")
	val groupsConsistency = groups.map { it.canonicalGroups.size }.average()
	println("Groups consistency: $groupsConsistency")
	val groupsSize = groups.map { it.ids.size }.average()
	println("Groups size: $groupsSize")

	val domains = corpus.urls.mapValues { (_, url) -> url.domainOrNull }
	val corpusIds: Set<String> = corpus.trecDocuments.mapTo(mutableSetOf()) { it.id }
	val groupIds: Set<String> = groups.flatMapTo(mutableSetOf()) { it.ids }
	val canonicalIds: Set<String> = groups.flatMapTo(mutableSetOf()) { it.canonicalIds }
	val canonicalizedIds: Set<String> = groups.flatMapTo(mutableSetOf()) { it.canonicalizedIds }
	println("${corpusIds.size}; ${groupIds.size}; ${canonicalIds.size}; ${canonicalizedIds.size}")
	println("Real copies in duplicates: ${canonicalizedIds.size.toDouble() / groupIds.size}")
	println("Real copies in corpus: ${canonicalizedIds.size.toDouble() / corpusIds.size}")
	val canonicalizedDomainIds = canonicalizedIds
			.mapToMap { it to domains[it] }
			.inverse()
	val groupDomainIds = groupIds
			.mapToMap { it to domains[it] }
			.inverse()
	val canonicalizedWikipediaIds = canonicalizedDomainIds
			.flatMap { (domain, ids) ->
				if (domain != null && "wikipedia.org" == domain.root) ids
				else emptyList()
			}
	val groupWikipediaIds = groupDomainIds
			.flatMap { (domain, ids) ->
				if (domain != null && "wikipedia.org" == domain.root) ids
				else emptyList()
			}
    println("Wikipedia docs in real copies: ${canonicalizedWikipediaIds.size.toDouble() / canonicalIds.size}")
    println("Real copies in Wikipedia docs: ${canonicalizedWikipediaIds.size.toDouble() / groupWikipediaIds.size}")
    val wikipediaGroups: Set<CanonicalFingerprintGroup> = groups
            .filterTo(mutableSetOf()) { group ->
                group.ids.any { it in groupWikipediaIds }
            }
    val consistentWikipediaGroups: Set<CanonicalFingerprintGroup> = wikipediaGroups
            .filterTo(mutableSetOf()) { group ->
                group.isConsistentCanonical
            }
    println("Consistent Wikipedia groups: ${consistentWikipediaGroups.size.toDouble() / wikipediaGroups.size}")
    val wikipediaGroupsConsistency = wikipediaGroups.map { it.canonicalGroups.size }.average()
    println("Wikipedia groups consistency: $wikipediaGroupsConsistency")
    val wikipediaGroupsSize = wikipediaGroups.map { it.ids.size }.average()
    println("Wikipedia groups size: $wikipediaGroupsSize")

    println()
    val inconsistentWikipediaGroups = (wikipediaGroups - consistentWikipediaGroups)
    val inconsistentWikipediaGroupCanonicalDocuments = inconsistentWikipediaGroups.flatMap { it.canonicalIds }
    val inconsistentWikipediaGroupCanonicalDocumentsNonWikipedia = inconsistentWikipediaGroupCanonicalDocuments
            .filterNot {
	            groupDomainIds.entries.find { (_, id) -> it in id }?.key?.root == "wikipedia.org"
            }
    println("Inconsistent wikipedia group documents: ${inconsistentWikipediaGroupCanonicalDocumentsNonWikipedia.size} of ${inconsistentWikipediaGroupCanonicalDocuments.size} (${inconsistentWikipediaGroupCanonicalDocumentsNonWikipedia.size.toDouble() / inconsistentWikipediaGroupCanonicalDocuments.size})")

    println()
    println("Domain stats: domain, relevance degree, no. of docs")
	canonicalizedDomainIds.entries
			.sortedByDescending { (_, ids) -> ids.size }
			.map { (domain, ids) ->
				val judgements = ids.flatMap { id ->
					corpus.trecTopics.mapNotNullTo(mutableListOf()) { topic ->
						topic[id]?.judgement
					}
				}
				"$domain: ${judgements.filter { it > 0 }.size.toDouble() / judgements.size}; ${ids.size}"
			}
            .forEach { println(it) }
}