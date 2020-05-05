package de.webis.webisstud.thesis.reimer.model

import java.io.File
import java.io.InputStream
import java.net.URL
import kotlin.reflect.KClass

class Resource(
        private val name: String,
        private val resolveRelativeTo: Class<*>
) {

    constructor(name: String, resolveRelativeTo: KClass<*>) : this(name, resolveRelativeTo.java)

    init {
        requireUrl()
    }

    fun file(): File {
        return createTempFile()
                .also(File::deleteOnExit)
                .also { file ->
                    file.outputStream().use { output ->
                        inputStream().use { input ->
                            input.copyTo(output)
                        }
                    }
                }
    }

    private fun findInputStream(): InputStream? = resolveRelativeTo.getResourceAsStream(name)

    private fun findUrl(): URL? = resolveRelativeTo.getResource(name)

    private fun requireInputStream() =
            requireNotNull(findInputStream()) { "Resource $name not found." }

    private fun requireUrl() =
            requireNotNull(findUrl()) { "Resource $name not found." }

    fun inputStream() = requireInputStream()
}