package de.webis.webisstud.thesis.reimer.model

interface RelevanceTopicResourceTask<DocumentType : JudgedDocument, TopicType : JudgedTopic<DocumentType>> : RelevanceResourceTask<DocumentType, TopicType>, TopicResourceTask<DocumentType, TopicType>
