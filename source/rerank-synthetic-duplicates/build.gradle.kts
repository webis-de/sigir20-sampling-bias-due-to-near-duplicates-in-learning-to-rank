plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":source:model"))
    implementation(project(":source:data"))
    implementation(project(":source:ltr"))
    implementation(project(":source:ltr-files"))
    implementation(project(":source:features-letor"))
}
