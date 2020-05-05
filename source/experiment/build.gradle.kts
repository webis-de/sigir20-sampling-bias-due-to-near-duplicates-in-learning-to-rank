import dev.reimer.spark.gradle.plugin.SparkSubmit

plugins {
	kotlin("jvm")
	id("com.github.johnrengelman.shadow") version "5.2.0"
	id("dev.reimer.spark") version "0.1.4"
}

dependencies {
	implementation(kotlin("stdlib-jdk8"))
	implementation(project(":source:model"))
	implementation(project(":source:data"))
	implementation(project(":source:ltr"))
	implementation(project(":source:ltr-files"))
	implementation(project(":source:groups"))
	implementation(project(":source:groups-canonicalization"))
	implementation(project(":source:corpus-url"))
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
	implementation("org.jetbrains.kotlinx:kotlinx-cli:0.2.1")
	implementation("dev.reimer:java-ktx:0.1.3")
	implementation("dev.reimer:spark-ktx:0.1.0")
	implementation("dev.reimer:domain-ktx:0.2.0")
	implementation("io.github.microutils:kotlin-logging:1.7.9")
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")
	testRuntimeOnly("org.junit.platform:junit-platform-runner:1.5.2")
}

tasks {
	test {
		useJUnitPlatform()
		testLogging {
			events("passed", "skipped", "failed")
		}
	}

	shadowJar {
		isZip64 = true
	}

	val corpora = listOf("ClueWeb09", "Gov2")
	val corporaTasksSpark = corpora.map { corpus ->
		@Suppress("UnstableApiUsage")
		register<SparkSubmit>("run${corpus.capitalize()}TrainingRerankingExperimentsSpark") {
			group = "experiment"
			description = "Run training/reranking experiments for $corpus corpus on Spark cluster."
			applicationResource.set(shadowJar.get().archiveFile)
			mainClass.set("de.webis.webisstud.thesis.reimer.experiment.RunExperimentsKt")
			applicationArguments.addAll("--spark", corpus)
			applicationName.set("$corpus LTR train/rerank")
			deployMode.set(SparkSubmit.DeployMode.Cluster)
			master.set("yarn")
			configuration.put("spark.executor.instances", 150)
			configuration.put("spark.dynamicAllocation.maxExecutors", 250)
			dependsOn(shadowJar)
		}
	}
	val corporaTasks = corpora.map { corpus ->
		register<JavaExec>("run${corpus.capitalize()}TrainingRerankingExperiments") {
			group = "experiment"
			description = "Run training/reranking experiments for $corpus corpus."
			main = "de.webis.webisstud.thesis.reimer.experiment.RunExperimentsKt"
			args("--debug", corpus)
			classpath = sourceSets.main.get().runtimeClasspath
		}
	}
	register("runTrainingRerankingExperimentsSpark") {
		group = "experiment"
		description = "Run training/reranking experiments for all corpora on Spark cluster."
		dependsOn(corporaTasksSpark)
	}
	register("runTrainingRerankingExperiments") {
		group = "experiment"
		description = "Run training/reranking experiments for all corpora."
		dependsOn(corporaTasks)
	}
}