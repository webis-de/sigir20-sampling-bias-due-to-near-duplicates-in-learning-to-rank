package de.webis.webisstud.thesis.reimer.evaluation.internal

import org.apache.spark.api.java.JavaRDD

fun <T, R> JavaRDD<T>.mapPartitionsWithIndex(
		preservesPartitioning: Boolean = false,
		transform: (partition: Int, Iterator<T>) -> Iterator<R>
): JavaRDD<R> =
		mapPartitionsWithIndex(transform, preservesPartitioning)

fun <T, R> JavaRDD<T>.mapPartitionsWithIndexSequence(
		preservesPartitioning: Boolean = false,
		transform: (partition: Int, Sequence<T>) -> Sequence<R>
): JavaRDD<R> = mapPartitionsWithIndex(preservesPartitioning) { partition, iterator ->
	transform(partition, iterator.asSequence()).iterator()
}
