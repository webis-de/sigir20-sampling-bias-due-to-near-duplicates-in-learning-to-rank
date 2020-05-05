import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.61" apply false
    kotlin("plugin.serialization") version "1.3.61" apply false
    id("de.undercouch.download") version "4.0.2" apply false
}

allprojects {
    repositories {
        jcenter()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://raw.githubusercontent.com/lintool/AnseriniMaven/master/mvn-repo/")
	    maven(url = "https://kotlin.bintray.com/kotlinx")
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
}

tasks {
    val build = register("build")
    val test = register("test")
    subprojects {
        build { dependsOn(tasks.named("build")) }
        test { dependsOn(tasks.named("test")) }
    }
}