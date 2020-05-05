package de.webis.webisstud.thesis.reimer.corpus.url

import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor
import java.net.URL

@Serializer(forClass = URL::class)
internal object UrlSerializer : KSerializer<URL> {
	override val descriptor: SerialDescriptor = StringDescriptor

	override fun deserialize(decoder: Decoder): URL {
		return URL(decoder.decodeString())
	}

	override fun serialize(encoder: Encoder, obj: URL) {
		encoder.encodeString(obj.toString())
	}
}