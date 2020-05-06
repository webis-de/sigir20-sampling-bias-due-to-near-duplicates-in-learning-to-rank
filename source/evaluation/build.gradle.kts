import dev.reimer.spark.gradle.plugin.SparkSubmit

plugins {
	kotlin("jvm")
	kotlin("plugin.serialization")
	id("com.github.johnrengelman.shadow") version "5.2.0"
	id("dev.reimer.spark") version "0.1.7"
}

dependencies {
	implementation(kotlin("stdlib-jdk8"))
	implementation(project(":source:model"))
	implementation(project(":source:ltr"))
	implementation(project(":source:corpus-url"))
	implementation(project(":source:groups"))
	implementation(project(":source:groups-canonicalization"))
	implementation(project(":source:data"))
	implementation(project(":source:math"))
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0")
	implementation("org.jetbrains.kotlinx:kotlinx-cli:0.2.1")
	implementation("dev.reimer:java-ktx:0.1.3")
	implementation("dev.reimer:domain-ktx:0.2.0")
	implementation("dev.reimer:jsonl-serialization:0.1.0")
	implementation("dev.reimer:spark-ktx:0.1.0")
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

	register<JavaExec>("runEvaluation") {
		group = "evaluation"
		description = "Run evaluations on reranked runs from experiment."
		main = "de.webis.webisstud.thesis.reimer.evaluation.RunEvaluationKt"
		args("--debug")
		classpath = sourceSets.main.get().runtimeClasspath
	}

	@Suppress("UnstableApiUsage")
	register<SparkSubmit>("runEvaluationSpark") {
		group = "evaluation"
		description = "Run evaluations on reranked runs from experiment on Spark cluster."
		applicationResource.set(shadowJar.flatMap { it.archiveFile })
		mainClass.set("de.webis.webisstud.thesis.reimer.evaluation.RunEvaluationKt")
		applicationArguments.addAll("--spark", "2020-05-06-04-12")
		applicationName.set("LTR evaluations")
		deployMode.set(SparkSubmit.DeployMode.Cluster)
		master.set("yarn")
		configuration.put("spark.executor.instances", 150)
		configuration.put("spark.dynamicAllocation.maxExecutors", 250)
		dependsOn(shadowJar)
	}

	register<JavaExec>("convertLegacyEvaluationResults") {
		group = "evaluation"
		description = "Convert evaluation results from legacy (JSON) format to new JSON-L format."
		main = "de.webis.webisstud.thesis.reimer.evaluation.LegacyEvaluationConversionKt"
		classpath = sourceSets.main.get().runtimeClasspath
	}

	register<JavaExec>("splitEvaluationResults") {
		group = "evaluation"
		description = "Split evaluation results per evaluation."
		main = "de.webis.webisstud.thesis.reimer.evaluation.SplitEvaluationsKt"
		args("2020-05-06-04-12")
		classpath = sourceSets.main.get().runtimeClasspath
	}
}