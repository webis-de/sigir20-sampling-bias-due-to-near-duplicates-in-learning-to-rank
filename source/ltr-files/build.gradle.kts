plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
	implementation(kotlin("stdlib-jdk8"))
	implementation(project(":source:model"))
	implementation(project(":source:search"))
	implementation(project(":source:groups"))
	implementation(project(":source:data"))
	implementation(project(":source:features-letor"))
	implementation("dev.reimer:java-ktx:0.1.3")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.13.0")
}

tasks {
	val corpora = listOf("ClueWeb09", "Gov2")
	val corporaTasks = corpora.map { corpus ->
		register<JavaExec>("compute${corpus.capitalize()}CorpusLtrFiles") {
			group = "corpus"
			description = "Compute the $corpus corpus' learning to rank files, and save to thesis data directory."
			main = "de.webis.webisstud.thesis.reimer.ltr.files.FilesKt"
			classpath = sourceSets.main.get().runtimeClasspath
			args(corpus)
		}
	}
	register("computeCorpusLtrFiles") {
		group = "corpus"
		description = "Compute all corpora's learning to rank files, and save to thesis data directory."
		dependsOn(corporaTasks)
	}
}
