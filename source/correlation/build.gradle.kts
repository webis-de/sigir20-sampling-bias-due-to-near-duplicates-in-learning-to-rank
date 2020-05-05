plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
	implementation(project(":source:model"))
	implementation(project(":source:data"))
	implementation(project(":source:corpus-url"))
	implementation(project(":source:math"))
	implementation(project(":source:groups"))
	implementation("dev.reimer:alexa-top-1m:0.2.0")
	implementation("dev.reimer:domain-ktx:0.2.0")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.0.1")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.13.0")
}
