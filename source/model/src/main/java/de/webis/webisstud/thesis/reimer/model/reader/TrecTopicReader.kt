package de.webis.webisstud.thesis.reimer.model.reader

import de.webis.webisstud.thesis.reimer.model.Document
import de.webis.webisstud.thesis.reimer.model.Topic
import de.webis.webisstud.thesis.reimer.model.reader.internal.AnseriniTopicReaderDelegate
import de.webis.webisstud.thesis.reimer.model.reader.internal.AnseriniTrecTopicReader

object TrecTopicReader : TopicReader<Document, Topic<Document>> by AnseriniTopicReaderDelegate(AnseriniTrecTopicReader())
