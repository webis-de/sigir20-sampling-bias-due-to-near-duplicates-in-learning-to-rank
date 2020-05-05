package de.webis.webisstud.thesis.reimer.model

interface JudgedTopic<DocumentType : JudgedDocument> : Topic<DocumentType>, Map<String, DocumentType>