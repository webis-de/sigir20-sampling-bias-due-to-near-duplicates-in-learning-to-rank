package de.webis.webisstud.thesis.reimer.data

import java.io.File
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object Data {
    @JvmStatic
    fun main(args: Array<String>) {
        println("Current Webis Ceph directory: $webisCephDir")
        println("Current data directory: $dataDir")
    }

    private class PathsDirectory(private vararg val paths: String?) {
        val directoryOrNull
            get() = paths
                    .asSequence()
                    .filterNotNull()
                    .map(::File)
                    .filter(File::exists)
                    .filter(File::isDirectory)
                    .firstOrNull()
        val directory
            get() = directoryOrNull
                    ?: error("Could not find directory, searched in: ${paths.filterNotNull().joinToString()}")
    }

    private val webisCephPaths = PathsDirectory(
            System.getenv("WEBIS_CEPH_HOME"),
            "/mnt/ceph/storage/",
            "W:/"
    )

    private val webisCephDir by lazy { webisCephPaths.directory }

    val webisCephGov2CorpusDir by lazy {
        webisCephDir.resolve("corpora/corpora-thirdparty/corpora-trec/corpus-trec-web/DOTGOV2/")
    }

    private val dataPaths = PathsDirectory(
            System.getenv("BACHELOR_THESIS_DATA"),
            webisCephPaths.directoryOrNull?.resolve("data-in-progress/wstud-thesis-reimer")?.path,
            "data"
    )

    private val dataDir = dataPaths.directory

    internal val corporaDir = dataDir.resolve("corpora")

    internal val tasksDir = dataDir.resolve("tasks")

    private val datasetsDir = dataDir.resolve("datasets")

    val letorDir = datasetsDir.resolve("letor")

    val experimentsDir = dataDir.resolve("experiments")

    private val formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd-HH-mm")

    fun experimentsDir(time: Instant) = experimentsDir
            .resolve(formatter.format(time.atOffset(ZoneOffset.UTC)))

    fun newExperimentsDir() = experimentsDir(Instant.now())

    val tempDir = dataDir.resolve("tmp")

    fun newTempDir() = createTempDir(directory = tempDir)

    fun newTempFile() = createTempFile(directory = tempDir)
}