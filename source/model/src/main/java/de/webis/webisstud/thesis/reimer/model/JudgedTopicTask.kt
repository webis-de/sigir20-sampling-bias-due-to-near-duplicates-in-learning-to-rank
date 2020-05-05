package de.webis.webisstud.thesis.reimer.model

interface JudgedTopicTask<DocumentType : JudgedDocument, TopicType : JudgedTopic<DocumentType>> : TopicTask<DocumentType, TopicType>