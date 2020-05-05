package de.webis.webisstud.thesis.reimer.model

interface RelevanceResourceTask<DocumentType : JudgedDocument, TopicType : JudgedTopic<DocumentType>> : JudgedTopicTask<DocumentType, TopicType> {
    val relevanceResource: Resource
}