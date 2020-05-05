import dev.reimer.spark.gradle.plugin.SparkSubmit
import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("dev.reimer.spark") version "0.1.4"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":source:model"))
    implementation(project(":source:data"))
    implementation(project(":source:corpus-url"))
    implementation("dev.reimer:spark-ktx:0.1.0")
}

tasks {

    shadowJar {
        isZip64 = true
    }

    fun registerClueWebTasks(id: String) {
        fun registerTask(main: String, name: String) {
            register<SparkSubmit>(
                    "spark${id.capitalize()}${main.substringAfterLast('.')}"
            ) {
                @Suppress("UnstableApiUsage")
                applicationResource.set(shadowJar.flatMap { it.archiveFile })
                mainClass.set(main)
                description = "$id $name"
                applicationName.set("Submit $description Spark job.")
                applicationArguments.add(id)
                val deployMode = when {
                    OperatingSystem.current().isWindows -> SparkSubmit.DeployMode.Client
                    else -> SparkSubmit.DeployMode.Cluster
                }
                this.deployMode.set(deployMode)
                val master = when {
                    OperatingSystem.current().isWindows -> "local[*]"
                    else -> "yarn"
                }
                this.master.set(master)
                @Suppress("UnstableApiUsage")
                configuration.put("spark.default.parallelism", 11)

                dependsOn(shadowJar)
            }
        }

        registerTask(
                "de.webis.webisstud.thesis.reimer.clueweb.graph.CorpusLinkCounts",
                "corpus inlink-outlink count"
        )
    }

    registerClueWebTasks("ClueWeb09")
    registerClueWebTasks("ClueWeb12")
}
