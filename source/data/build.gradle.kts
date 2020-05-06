import java.util.Properties

plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":source:model"))
}

tasks {
    register<JavaExec>("printDataPaths") {
        group = "help"
        description = "Print current Webis Ceph, and thesis data directories."
        main = "de.webis.webisstud.thesis.reimer.data.Data"
        classpath = sourceSets.main.get().runtimeClasspath
    }

    val createProperties = register("createLocalDataProperties") {
        val dataPath = rootProject.file("data").absolutePath
        val propertiesFile = sourceSets
            .main
            .get()
            .resources
            .sourceDirectories
            .first()
            .resolve("data.properties")
        doLast {
            val properties = Properties()
            properties["data.path"] = dataPath
            propertiesFile.writer().use {
                properties.store(it, null)
            }
        }
        inputs.property("data path", dataPath)
        outputs.file(propertiesFile)
    }

    classes {
        dependsOn(createProperties)
    }
}
