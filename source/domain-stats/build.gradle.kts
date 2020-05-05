plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
	implementation(project(":source:model"))
	implementation(project(":source:groups"))
	implementation(project(":source:data"))
	implementation(project(":source:corpus-url"))
	implementation("dev.reimer:java-ktx:0.1.3")
	implementation("dev.reimer:domain-ktx:0.2.0")
	implementation("dev.reimer:jsonl-serialization:0.1.0")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.13.0")
}

tasks {
	val corpora = listOf("ClueWeb09", "ClueWeb12", "Gov2")
	val corporaTasks = corpora.map { corpus ->
		register<JavaExec>("compute${corpus.capitalize()}CorpusDomainStats") {
			group = "corpus"
			description = "Compute the $corpus corpus' domain statistics, and save to thesis data directory."
			main = "de.webis.webisstud.thesis.reimer.domain.stats.DomainStatsKt"
			classpath = sourceSets.main.get().runtimeClasspath
			args(corpus)
		}
	}
	val corporaTasksRedundant = corpora.map { corpus ->
		register<JavaExec>("compute${corpus.capitalize()}CorpusRedundantDomainStats") {
			group = "corpus"
			description = "Compute the $corpus corpus' domain statistics for redundant documents only, and save to thesis data directory."
			main = "de.webis.webisstud.thesis.reimer.domain.stats.DomainStatsKt"
			classpath = sourceSets.main.get().runtimeClasspath
			args(corpus, "--redundant")
		}
	}
	register("computeCorpusDomainStats") {
		group = "corpus"
		description = "Compute all corpora's domain statistics, and save to thesis data directory."
		dependsOn(corporaTasks)
	}
	register("computeCorpusRedundantDomainStats") {
		group = "corpus"
		description = "Compute all corpora's domain statistics for redundant documents only, and save to thesis data directory."
		dependsOn(corporaTasksRedundant)
	}
}
