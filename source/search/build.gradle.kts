plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":source:model"))
    api("com.github.janheinrichmerker:elasticsearch-kotlin-dsl:0.1.2")
    api("org.elasticsearch.client:elasticsearch-rest-high-level-client:6.8.2")
}
