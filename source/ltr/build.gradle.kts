plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
	implementation(project(":source:model"))
	implementation(project(":source:data"))
	implementation(project(":source:groups"))
	implementation(project(":source:ltr-files"))
	implementation(project(":source:features-letor"))
	implementation("dev.reimer:jsonl-serialization:0.1.0")
	implementation("org.codelibs:ranklib:2.10.1")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0")
}
