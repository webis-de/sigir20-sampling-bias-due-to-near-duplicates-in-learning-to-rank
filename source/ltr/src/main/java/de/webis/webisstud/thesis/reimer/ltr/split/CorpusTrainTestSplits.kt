package de.webis.webisstud.thesis.reimer.ltr.split

import de.webis.webisstud.thesis.reimer.model.Corpus

enum class CorpusTrainTestSplits(
        val corpus: Corpus,
        splits: Set<FeatureVectorSplitter>
) : Set<FeatureVectorSplitter> by splits {
    ClueWeb09(Corpus.ClueWeb09, LetorSplits(Corpus.ClueWeb09)),
    Gov2(Corpus.Gov2, LetorSplits(Corpus.Gov2))
}