package de.webis.webisstud.thesis.reimer.model

interface Topic<DocumentType : Document> : Map<String, DocumentType>, Iterable<DocumentType>, Comparable<Topic<DocumentType>> {
    val task: Task
    val id: String
    val title: String
    val description: String? get() = null
    val narrative: String? get() = null

    fun getDocument(key: String): DocumentType = getValue(key)

    val documents: Set<DocumentType> get() = toSet()

    override fun iterator(): Iterator<DocumentType> {
        return values.iterator()
    }

    override fun compareTo(other: Topic<DocumentType>) = id.compareTo(other.id)
}
