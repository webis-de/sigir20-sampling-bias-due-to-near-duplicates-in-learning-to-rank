package de.webis.webisstud.thesis.reimer.letor

enum class QuerySet(id: String) {
    MillionQuery2007("MQ2007"),
    MillionQuery2008("MQ2008"),
    ClueWeb09("ClueWeb09");

    val supervised = NullMinNormDataset(
            querySetId = id,
            rankingSettingTag = null,
            nullVectorsName = "NULL",
            minVectorsName = "min",
            normVectorsName = "Querylevelnorm",
            normVectorsFoldsNamePrefix = "S"
    )

    val semiSupervised = NullMinNormDataset(
            querySetId = id,
            rankingSettingTag = "semi",
            nullVectorsName = "Large_NULL",
            minVectorsName = "Large_min",
            normVectorsName = "Large_norm",
            normVectorsFoldsNamePrefix = "L"
    )

    val aggregation = AggregationDataset(
            querySetId = id,
            rankingSettingTag = "agg",
            aggregationVectorsName = "agg",
            aggregationVectorsFoldsNamePrefix = "A"
    )

    val listWise = NullMinNormDataset(
            querySetId = id,
            rankingSettingTag = "list",
            nullVectorsName = "List_NULL",
            minVectorsName = "List_min",
            normVectorsName = "List_norm",
            normVectorsFoldsNamePrefix = "I"
    )
}
