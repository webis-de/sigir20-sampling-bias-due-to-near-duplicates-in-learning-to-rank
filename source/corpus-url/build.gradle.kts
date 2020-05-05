plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":source:model"))
    implementation("dev.reimer:jsonl-serialization:0.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.13.0")
}
