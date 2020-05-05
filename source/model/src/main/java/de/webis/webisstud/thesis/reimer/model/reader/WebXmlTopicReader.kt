package de.webis.webisstud.thesis.reimer.model.reader

import de.webis.webisstud.thesis.reimer.model.Document
import de.webis.webisstud.thesis.reimer.model.Topic
import de.webis.webisstud.thesis.reimer.model.reader.internal.AnseriniTopicReaderDelegate
import de.webis.webisstud.thesis.reimer.model.reader.internal.AnseriniWebXmlTopicReader

object WebXmlTopicReader : TopicReader<Document, Topic<Document>> by AnseriniTopicReaderDelegate(AnseriniWebXmlTopicReader())
