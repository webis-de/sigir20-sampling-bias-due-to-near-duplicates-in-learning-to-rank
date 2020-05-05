package de.webis.webisstud.thesis.reimer.model

interface TopicResourceTask<DocumentType : Document, TopicType : Topic<DocumentType>> : TopicTask<DocumentType, TopicType> {
    val topicResource: Resource
}