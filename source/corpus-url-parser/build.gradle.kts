plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":source:model"))
    implementation(project(":source:data"))
    implementation(project(":source:corpus-url"))
    implementation(project(":source:search"))
    implementation("dev.reimer:java-ktx:0.1.3")
}

tasks {
    val corpora = listOf("ClueWeb09", "ClueWeb12", "Gov2")
    val corporaTasks = corpora.map { corpus ->
        register<JavaExec>("compute${corpus.capitalize()}CorpusUrls") {
            group = "corpus"
            description = "Compute the $corpus corpus' document URL mappings, and save to thesis data directory."
            main = "de.webis.webisstud.thesis.reimer.corpus.url.ComputeUrlsKt"
            classpath = sourceSets.main.get().runtimeClasspath
            args(corpus)
        }
    }
    register("computeCorpusUrls") {
        group = "corpus"
        description = "Compute all corpora's document URL mappings, and save to thesis data directory."
        dependsOn(corporaTasks)
    }
}
