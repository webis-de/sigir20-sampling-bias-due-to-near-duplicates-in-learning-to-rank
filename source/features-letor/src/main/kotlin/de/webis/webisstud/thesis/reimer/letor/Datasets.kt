package de.webis.webisstud.thesis.reimer.letor

import de.webis.webisstud.thesis.reimer.data.Data
import java.io.File

private const val FOLD_COUNT = 5
private val DATA_DIRECTORY = Data.featuresDir

sealed class Dataset(querySetId: String, rankingSettingTag: String?) {

    private val name = "${querySetId}${if (rankingSettingTag == null) "" else "-$rankingSettingTag"}"
    val path = DATA_DIRECTORY.resolve(name)
    val archive = DATA_DIRECTORY.resolve("$name.rar") // TODO Un-RAR archive if [path] is not found.

    val partitionFolds by lazy {
        val partitions: List<File> = path
                .listFiles { file: File -> file.isDirectory }
                ?.toList()
                ?: emptyList()
        partitions.map { Partitions(it) }
    }
}

class AggregationDataset internal constructor(
        querySetId: String,
        rankingSettingTag: String?,
        aggregationVectorsName: String,
        aggregationVectorsFoldsNamePrefix: String
) : Dataset(querySetId, rankingSettingTag) {
    val aggregationVectors = path.resolve("$aggregationVectorsName.txt")
    val aggregationVectorsFolds = (1..FOLD_COUNT)
            .map { path.resolve("$aggregationVectorsFoldsNamePrefix$it.txt") }
}

class NullMinNormDataset internal constructor(
        querySetId: String,
        rankingSettingTag: String?,
        nullVectorsName: String,
        minVectorsName: String,
        normVectorsName: String,
        normVectorsFoldsNamePrefix: String
) : Dataset(querySetId, rankingSettingTag) {
    val nullVectors = path.resolve("$nullVectorsName.txt")
    val minVectors = path.resolve("$minVectorsName.txt")
    val normVectors = path.resolve("$normVectorsName.txt")
    val normVectorsFolds = (1..FOLD_COUNT)
            .map { path.resolve("$normVectorsFoldsNamePrefix$it.txt") }
}