import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.10"
    application
}


group = "de.seatalyser"
version = "1.0-SNAPSHOT"


val jacksonVersion = "2.15.2"

repositories {
    mavenCentral()
}


fun DependencyHandler.jacksonCore(name: String) {
    implementation("com.fasterxml.jackson.core", "jackson-$name", jacksonVersion)
}


fun DependencyHandler.jackson(group: String, name: String) {
    implementation("com.fasterxml.jackson.$group", "jackson-$group-$name", jacksonVersion)
}

dependencies {
    implementation("de.bixilon:kutil:1.23.2")
    testImplementation("org.testng:testng:7.7.0")
    jacksonCore("core")
    jacksonCore("databind")
    jackson("module", "kotlin")
    jackson("datatype", "jsr310")
    implementation("org.jsoup:jsoup:1.16.1")

    implementation("io.ktor:ktor-server-core:2.3.4")
    implementation("io.ktor:ktor-server-netty:2.3.4")

    testImplementation(kotlin("test"))
}

tasks.test {
    useTestNG()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("de.seatalyser.Seatalyser")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.languageVersion = "2.0"
}
