plugins {
    kotlin("jvm") version "1.8.0"
    application
    id("io.gitlab.arturbosch.detekt") version "1.0.0.RC6-4"
}

detekt {
    version = "1.0.0.RC6-4"
    defaultProfile {
        input = "$projectDir/src/main/kotlin"
        config = "$projectDir/default-detekt-config.yml"
        filters = ".*/res/.*,.*build/.*"
    }
}

group = "shmax"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("com.squareup.retrofit2:converter-jackson:2.9.0")
    implementation("com.google.guava:guava:31.1-jre")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}