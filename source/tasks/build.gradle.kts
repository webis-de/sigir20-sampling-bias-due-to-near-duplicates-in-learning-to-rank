plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("dev.reimer.spark") version "0.1.4"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":source:model"))
    implementation(project(":source:ltr"))
    implementation(project(":source:groups"))
    implementation(project(":source:groups-canonicalization"))
    implementation(project(":source:data"))
    implementation(project(":source:correlation"))
    implementation(project(":source:corpus-url"))
    implementation(project(":source:math"))
    implementation("dev.reimer:alexa-top-1m:0.2.0")
    implementation("dev.reimer:java-ktx:0.1.3")
    implementation("dev.reimer:domain-ktx:0.2.0")
    implementation("org.knowm.xchart:xchart:3.6.0")
}
