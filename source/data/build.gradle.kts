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
}
