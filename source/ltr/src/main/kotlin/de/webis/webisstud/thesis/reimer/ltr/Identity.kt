package de.webis.webisstud.thesis.reimer.ltr

import de.webis.webisstud.thesis.reimer.ltr.sampling.Mutation
import de.webis.webisstud.thesis.reimer.ltr.sampling.RunSampling
import de.webis.webisstud.thesis.reimer.ltr.sampling.Sampling
import de.webis.webisstud.thesis.reimer.ltr.split.Split
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.FeatureVector

object Identity : Sampling, Mutation, RunSampling {
	override val id = "identity"
	override fun sample(items: List<JudgedRunLine>, corpus: Corpus) = items
	override fun sample(items: Collection<FeatureVector>, split: Split, corpus: Corpus) = items
	override fun mutate(items: Iterable<FeatureVector>, split: Split, corpus: Corpus) = items
}