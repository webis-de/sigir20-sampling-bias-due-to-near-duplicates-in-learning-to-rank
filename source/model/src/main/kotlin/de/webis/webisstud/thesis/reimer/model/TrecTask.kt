package de.webis.webisstud.thesis.reimer.model

import de.webis.webisstud.thesis.reimer.model.reader.*

/**
 * Copied from [Webis NDD's `SharedTask`](https://github.com/webis-de/trec-near-duplicates/blob/master/src/main/java/de/webis/trec_ndd/trec_collections/SharedTask.java)
 */
enum class TrecTask(
        override val corpus: Corpus,
        relevanceFilePath: String,
        relevanceReader: RelevanceReader = QrelReader,
        topicFilePath: String,
        topicReader: TopicReader<Document, Topic<Document>> = TrecTopicReader,
        override val title: String,
        override val description: String? = null,
        override val url: String? = null
) : RelevanceTopicResourceTask<JudgedDocument, JudgedTopic<JudgedDocument>>, Map<String, JudgedTopic<JudgedDocument>> {

    Web2009(
            corpus = Corpus.ClueWeb09,
            relevanceFilePath = "/topics-and-qrels/web/prels.1-50.txt",
            relevanceReader = PrelReader,
            topicFilePath = "/topics-and-qrels/web/topics.1-50.txt",
            topicReader = WebXmlTopicReader,
            title = "TREC 2009 Web Track",
            url = "https://trec.nist.gov/data/web09.html"
    ),
    Web2010(
            corpus = Corpus.ClueWeb09,
            relevanceFilePath = "/topics-and-qrels/web/qrels.51-100.txt",
            topicFilePath = "/topics-and-qrels/web/topics.51-100.txt",
            topicReader = WebXmlTopicReader,
            title = "TREC 2010 Web Track",
            url = "https://trec.nist.gov/data/web10.html"
    ),
    Web2011(
            corpus = Corpus.ClueWeb09,
            relevanceFilePath = "/topics-and-qrels/web/qrels.101-150.txt",
            topicFilePath = "/topics-and-qrels/web/topics.101-150.txt",
            topicReader = WebXmlTopicReader,
            title = "TREC 2011 Web Track",
            url = "https://trec.nist.gov/data/web11.html"
    ),
    Web2012(
            corpus = Corpus.ClueWeb09,
            relevanceFilePath = "/topics-and-qrels/web/qrels.151-200.txt",
            topicFilePath = "/topics-and-qrels/web/topics.151-200.txt",
            topicReader = WebXmlTopicReader,
            title = "TREC 2012 Web Track",
            url = "https://trec.nist.gov/data/web12.html"
    ),
    Web2013(
            corpus = Corpus.ClueWeb12,
            relevanceFilePath = "/topics-and-qrels/web/qrels.201-250.txt",
            topicFilePath = "/topics-and-qrels/web/topics.201-250.txt",
            topicReader = WebXmlTopicReader,
            title = "TREC 2013 Web Track",
            url = "https://trec.nist.gov/data/web13.html"
    ),
    Web2014(
            corpus = Corpus.ClueWeb12,
            relevanceFilePath = "/topics-and-qrels/web/qrels.251-300.txt",
            topicFilePath = "/topics-and-qrels/web/topics.251-300.txt",
            topicReader = WebXmlTopicReader,
            title = "TREC 2014 Web Track",
            url = "https://trec.nist.gov/data/web14.html"
    ),
    MillionQuery2007(
            corpus = Corpus.Gov2,
            relevanceFilePath = "/topics-and-qrels/millionquery/prels.1-10000.txt",
            relevanceReader = PrelReader,
            topicFilePath = "/topics-and-qrels/millionquery/topics.1-10000.txt",
            topicReader = LineTopicReader,
            title = "TREC 2007 Million Query Track",
            url = "https://trec.nist.gov/data/million.query07.html"
    ),
    MillionQuery2008(
            corpus = Corpus.Gov2,
            relevanceFilePath = "/topics-and-qrels/millionquery/prels.10001-20000.txt",
            relevanceReader = PrelReader,
            topicFilePath = "/topics-and-qrels/millionquery/topics.10001-20000.txt",
            topicReader = LineTopicReader,
            title = "TREC 2008 Million Query Track",
            url = "https://trec.nist.gov/data/million.query08.html"
    ),
    MillionQuery2009(
            corpus = Corpus.Gov2,
            relevanceFilePath = "/topics-and-qrels/millionquery/prels.20001-60000.txt",
            relevanceReader = PrelReader,
            topicFilePath = "/topics-and-qrels/millionquery/topics.20001-60000.txt",
            topicReader = LineTopicReader,
            title = "TREC 2009 Million Query Track",
            url = "https://trec.nist.gov/data/million.query09.html"
    );

    override val id = "trec-${name.toLowerCase()}"

    override val topicResource = Resource(topicFilePath, TrecTask::class)
    override val relevanceResource = Resource(relevanceFilePath, TrecTask::class)

    private val parser = RelevanceTopicResourceParser(this, topicReader, relevanceReader)

    override val entries get() = parser.entries
    override val keys get() = parser.keys
    override val size get() = parser.size
    override val values get() = parser.values
    override fun containsKey(key: String) = parser.containsKey(key)
    override fun containsValue(value: JudgedTopic<JudgedDocument>) = parser.containsValue(value)
    override fun get(key: String) = parser[key]
    override fun isEmpty() = parser.isEmpty()

    companion object {
        fun find(name: String): TrecTask? {
            val lowerCaseName = name.toLowerCase()
            return values().first { it.name.toLowerCase() == lowerCaseName }
        }

        fun valueOfCaseInsensitive(name: String): TrecTask {
            return requireNotNull(find(name)) {
                "No value for name '$name'."
            }
        }
    }
}
