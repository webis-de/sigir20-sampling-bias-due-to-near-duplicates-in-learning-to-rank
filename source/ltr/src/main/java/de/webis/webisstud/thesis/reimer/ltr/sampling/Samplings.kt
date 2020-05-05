package de.webis.webisstud.thesis.reimer.ltr.sampling

import de.webis.webisstud.thesis.reimer.ltr.JudgedRunLine
import de.webis.webisstud.thesis.reimer.ltr.split.Split
import de.webis.webisstud.thesis.reimer.model.Corpus
import de.webis.webisstud.thesis.reimer.model.FeatureVector

fun Collection<FeatureVector>.sample(
		sampling: Sampling,
		split: Split,
		corpus: Corpus
) = sampling.sample(this, split, corpus)

operator fun Sampling.plus(sampling: Sampling): Sampling {
    return object : Sampling {
        override val id = "${this@plus.id}-${sampling.id}"
        override fun sample(items: Collection<FeatureVector>, split: Split, corpus: Corpus) =
                sampling.sample(this@plus.sample(items, split, corpus), split, corpus)
    }
}

fun Iterable<FeatureVector>.mutate(
		sampling: Mutation,
		split: Split,
		corpus: Corpus
) = sampling.mutate(this, split, corpus)

operator fun Mutation.plus(mutation: Mutation): Mutation {
    return object : Mutation {
        override val id = "${this@plus.id}-${mutation.id}"
        override fun mutate(items: Iterable<FeatureVector>, split: Split, corpus: Corpus) =
                mutation.mutate(this@plus.mutate(items, split, corpus), split, corpus)
    }
}

fun List<JudgedRunLine>.sample(
		sampling: RunSampling,
		corpus: Corpus
) = sampling.sample(this, corpus)

operator fun RunSampling.plus(sampling: RunSampling): RunSampling {
    return object : RunSampling {
	    override val id = "${this@plus.id}-${sampling.id}"
	    override fun sample(items: List<JudgedRunLine>, corpus: Corpus) =
			    sampling.sample(this@plus.sample(items, corpus), corpus)
    }
}