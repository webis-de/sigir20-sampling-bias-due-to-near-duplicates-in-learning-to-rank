package de.webis.webisstud.thesis.reimer.correlation

import kotlinx.serialization.*
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.internal.NamedListClassDescriptor
import kotlinx.serialization.internal.SerialClassDescImpl
import kotlinx.serialization.internal.StringDescriptor

@Serializable(with = CorrelationMatrix.InternalSerializer::class)
class CorrelationMatrix<T> {

    @SerialName("correlations")
    private val data: MutableMap<SymmetricPair<T>, Double> = mutableMapOf()

    operator fun set(first: T, second: T, correlation: Double) {
        data[first to second] = correlation
    }

    operator fun get(first: T, second: T) = data.getValue(first to second)

    override fun toString(): String {
        return data.entries.joinToString(
                prefix = "Correlations=[",
                postfix = "]",
                separator = ", "
        ) { (pair, correlation) ->
            "τ$pair=$correlation"
        }
    }

    private class SymmetricPair<T>(
            first: T,
            second: T
    ) {

        private fun findFirst(first: T, second: T) =
                if (first.hashCode() <= second.hashCode()) first else second

        private fun findSecond(first: T, second: T) =
                if (first.hashCode() <= second.hashCode()) second else first

        val first: T = findFirst(first, second)
        val second: T = findSecond(first, second)

        /**
         * Returns string representation of the [SymmetricPair] including its [first] and [second] values.
         */
        override fun toString(): String = "($first, $second)"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is SymmetricPair<*>) return false
            if (first != other.first) return false
            if (second != other.second) return false
            return true
        }

        override fun hashCode(): Int {
            var result = first?.hashCode() ?: 0
            result = 31 * result + (second?.hashCode() ?: 0)
            return result
        }
    }

    @Serializer(forClass = CorrelationMatrix::class)
    internal class InternalSerializer<T>(
            innerSerializer: KSerializer<T>
    ) :
            KSerializer<CorrelationMatrix<T>> {

        private val serializer = ArrayListSerializer(ElementSerializer(innerSerializer))

        override val descriptor: SerialDescriptor = StringDescriptor.withName("CorrelationMatrix")

        override fun serialize(encoder: Encoder, obj: CorrelationMatrix<T>) {
            val entries = obj.data
                    .map { Entry(it.key, it.value) }
            encoder.encodeSerializableValue(serializer, entries)
        }

        override fun deserialize(decoder: Decoder): CorrelationMatrix<T> {
            val entries = decoder.decodeSerializableValue(serializer)
            val matrix = CorrelationMatrix<T>()
            for (entry in entries) {
                matrix[entry.pair.first, entry.pair.second] = entry.correlation
            }
            return matrix
        }

        private data class Entry<T>(
                val pair: SymmetricPair<T>,
                val correlation: Double
        )

        @Serializer(forClass = Entry::class)
        private class ElementSerializer<T>(
                private val innerSerializer: KSerializer<T>
        ) : KSerializer<Entry<T>> {
            val elementDescriptor: SerialDescriptor = object : SerialClassDescImpl("SymmetricPair") {
                init {
                    addElement("firstRank")
                    addElement("secondRank")
                    addElement("correlation")
                }
            }
            override val descriptor = NamedListClassDescriptor("SymmetricPair", elementDescriptor)

            override fun serialize(encoder: Encoder, obj: Entry<T>) {
                encoder.beginStructure(elementDescriptor).run {
                    encodeSerializableElement(elementDescriptor, 0, innerSerializer, obj.pair.first)
                    encodeSerializableElement(elementDescriptor, 1, innerSerializer, obj.pair.second)
                    encodeDoubleElement(elementDescriptor, 2, obj.correlation)
                    endStructure(elementDescriptor)
                }
            }

            override fun deserialize(decoder: Decoder): Entry<T> {
                return decoder.beginStructure(elementDescriptor).run {
                    val first = decodeSerializableElement(elementDescriptor, 0, innerSerializer)
                    val second = decodeSerializableElement(elementDescriptor, 1, innerSerializer)
                    val correlation = decodeDoubleElement(elementDescriptor, 2)
                    endStructure(elementDescriptor)
                    Entry(SymmetricPair(first, second), correlation)
                }
            }
        }
    }


    /**
     * Creates a tuple of type [SymmetricPair] from this and [that].
     */
    private infix fun <T> T.to(that: T): SymmetricPair<T> = SymmetricPair(this, that)
}