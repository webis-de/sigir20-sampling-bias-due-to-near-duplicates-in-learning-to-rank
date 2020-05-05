plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":source:model"))
    implementation("com.github.chatnoir-eu:webis-uuid:4f715357df")
}
