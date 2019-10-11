import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.google.cloud.tools.jib") version "2.2.0"

    id("org.springframework.boot") version "2.2.6.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
}

group = "io.suppie"
version = "1.0.1"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

jib {
    to {
        // Tagging our image as recommended by Google
        // https://cloud.google.com/solutions/best-practices-for-building-containers#tagging_using_semantic_versioning
        tags = setOf(
                // Specific X.Y.Z version
                project.version.toString(),
                // Latest patch release of the X.Y minor branch
                project.version.toString().substringBeforeLast("."),
                // Latest patch release of the latest minor release of the X major branch
                project.version.toString().substringBefore("."),
                // Most recent (possibly stable) image
                "latest"
        )
    }
    container {
        ports = listOf("8080")
        // Good list of default flags intended for Java 8 (>= 8u191) containers
        jvmFlags = listOf(
                "-server",
                "-Djava.awt.headless=true",
                "-XX:InitialRAMFraction=2",
                "-XX:MinRAMFraction=2",
                "-XX:MaxRAMFraction=2",
                "-XX:+UseG1GC",
                "-XX:MaxGCPauseMillis=100",
                "-XX:+UseStringDeduplication"
        )
    }
}
