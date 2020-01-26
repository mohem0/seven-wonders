plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot") version "2.2.4.RELEASE"
    id("org.jlleitschuh.gradle.ktlint")
}

apply(plugin = "io.spring.dependency-management")

dependencies {
    compile(project(":sw-common-model"))
    compile(project(":sw-engine"))
    compile(kotlin("stdlib-jdk8"))
    compile(kotlin("reflect")) // required by Spring 5

    compile("org.springframework.boot:spring-boot-starter-websocket")
    compile("org.springframework.boot:spring-boot-starter-security")
    // required by spring security when using websockets
    compile("org.springframework.security:spring-security-messaging")

    compile("com.fasterxml.jackson.module:jackson-module-kotlin")

    compile("ch.qos.logback:logback-classic:1.1.8")
    compile("org.hildan.livedoc:livedoc-springboot:4.3.2")
    compile("org.hildan.livedoc:livedoc-ui-webjar:4.3.2")

    annotationProcessor("org.hildan.livedoc:livedoc-javadoc-processor:4.3.2")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation(project(":sw-client"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.hildan.jackstomp:jackstomp:2.0.0")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0-M1")
}

// packages the frontend app within the jar
tasks.bootJar {
    from("../sw-ui-kt/build/processedResources/Js/main") {
        into("static")
    }
}

// make sure we build the frontend before creating the jar
tasks.bootJar.get().dependsOn(":sw-ui-kt:assemble")
