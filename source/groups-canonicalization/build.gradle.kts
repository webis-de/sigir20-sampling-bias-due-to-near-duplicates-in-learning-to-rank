plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
	implementation(project(":source:model"))
	implementation(project(":source:data"))
	implementation(project(":source:groups"))
	implementation(project(":source:chatnoir"))
	implementation(project(":source:corpus-url"))
	implementation("dev.reimer:jsonl-serialization:0.1.0")
	implementation("dev.reimer:url-canonicalization:0.1.1")
	implementation("dev.reimer:java-ktx:0.1.3")
	implementation("dev.reimer:domain-ktx:0.2.0")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.13.0")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
}

tasks {
	val corpora = listOf("ClueWeb09", "ClueWeb12", "Gov2")
	val corporaTasks = corpora.map { corpus ->
		register<JavaExec>("compute${corpus.capitalize()}CorpusCanonicalFingerprintGroups") {
			group = "corpus"
			description = "Compute the $corpus corpus' canonical fingerprint groups, and save to thesis data directory."
			main = "de.webis.webisstud.thesis.reimer.groups.canonical.FingerprintGroupsCanonicalizerKt"
			classpath = sourceSets.main.get().runtimeClasspath
			args(corpus)
		}
	}
	register("computeCorpusCanonicalFingerprintGroups") {
		group = "corpus"
		description = "Compute all corpora's canonical fingerprint groups, and save to thesis data directory."
		dependsOn(corporaTasks)
	}
}
