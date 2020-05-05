plugins {
    kotlin("jvm")
	kotlin("plugin.serialization")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
	implementation(project(":source:model"))
	implementation(project(":source:data"))
	implementation(project(":source:search"))
	implementation("dev.reimer:java-ktx:0.1.3")
	implementation("dev.reimer:jsonl-serialization:0.1.0")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.13.0")
	implementation("org.jetbrains.kotlinx:kotlinx-cli:0.2.1")
}

tasks {
	register<JavaExec>("makeClueWeb09Features") {
		group = "datasets"
		description = "Make learning to rank features for ClueWeb09 corpus."
		main = "de.webis.webisstud.thesis.reimer.features.clueweb09.MakeFeaturesKt"
		args("--debug")
		classpath = sourceSets.main.get().runtimeClasspath
	}
}