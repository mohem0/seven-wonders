plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":sw-common-model"))
    implementation("com.github.salomonbrys.kotson:kotson:2.5.0")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}
