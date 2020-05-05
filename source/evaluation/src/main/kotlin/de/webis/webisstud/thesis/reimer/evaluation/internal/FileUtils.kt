package de.webis.webisstud.thesis.reimer.evaluation.internal

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.FileUtil
import org.apache.hadoop.fs.Path
import java.io.File
import java.io.Writer
import java.nio.charset.Charset

fun File.hadoopCopyMergeTo(out: File, conf: Configuration) {
	out.delete()
	FileUtil.copyMerge(
			FileSystem.getLocal(conf),
			Path(absolutePath),
			FileSystem.getLocal(conf),
			Path(out.absolutePath),
			true,
			conf,
			null
	)
}

fun File.appendLine(
		line: String,
		charset: Charset = Charsets.UTF_8
): Unit = appendText("$line\n", charset)

fun File.copyTo(writer: Writer) = bufferedReader().use { it.copyTo(writer) }

fun File.concatFilesTo(outFile: File) = listFiles()!!.asIterable().mergeTo(outFile)

fun Iterable<File>.mergeTo(outFile: File) {
	outFile.bufferedWriter().use { writer ->
		forEach { file ->
			file.copyTo(writer)
			writer.flush()
		}
	}
}
