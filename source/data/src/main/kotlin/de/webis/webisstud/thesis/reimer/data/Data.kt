package de.webis.webisstud.thesis.reimer.data

import de.webis.webisstud.thesis.reimer.model.Resource
import de.webis.webisstud.thesis.reimer.model.resource
import java.io.File
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

object Data {
    @JvmStatic
    fun main(args: Array<String>) {
        println("Current Webis Ceph directory: $webisCephDir")
        println("Current data directory: $dataDir")
    }

    private val dataProperties = Properties().apply {
        load(Resource("/data.properties", Data::class).inputStream())
    }

    private class PathsDirectory(private vararg val paths: String?) {
        val pathFiles
            get() = paths
                .asSequence()
                .filterNotNull()
                .map(::File)
        val directoryOrNull
            get() = pathFiles
                .filter(File::exists)
                .filter(File::isDirectory)
                .firstOrNull()
        val directory
            get() = directoryOrNull
                ?: error("Could not find directory, searched in: ${pathFiles.joinToString { it.absolutePath }}")
    }

    private val webisCephPaths = PathsDirectory(
        "/mnt/ceph/storage/",
        "W:/"
    )

    private val webisCephDir by lazy { webisCephPaths.directory }

    val webisCephGov2CorpusDir by lazy {
        webisCephDir.resolve("corpora/corpora-thirdparty/corpora-trec/corpus-trec-web/DOTGOV2/")
    }

    private val dataPaths = PathsDirectory(
        webisCephPaths.directoryOrNull?.resolve("data-in-progress/wstud-thesis-reimer")?.path,
        dataProperties.getProperty("data.path"),
        "data"
    )

    private val dataDir by lazy { dataPaths.directory }

    internal val corporaDir by lazy { dataDir.resolve("corpora").apply { mkdir() } }

    internal val tasksDir by lazy { dataDir.resolve("tasks").apply { mkdir() } }

    val featuresDir by lazy { dataDir.resolve("features").apply { mkdir() } }

    private val experimentsDir by lazy { dataDir.resolve("experiments").apply { mkdir() } }

    private val formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd-HH-mm")

    fun experimentsDir(time: Instant) = experimentsDir
        .resolve(formatter.format(time.atOffset(ZoneOffset.UTC)))

    fun newExperimentsDir() = experimentsDir(Instant.now())

    private val tempDir by lazy { dataDir.resolve("tmp").apply { mkdir() } }

    fun newTempDir() = createTempDir(directory = tempDir)
}