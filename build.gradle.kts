plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.cloud.tools.jib") version "3.4.5"
}

group = "nl.cqit.tools"
version = project.findProperty("version")?.takeUnless { it == "unspecified" }?.toString() ?: "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.retry:spring-retry")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.rocksdb:rocksdbjni:7.10.2")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.mockito", module = "mockito-core")
    }
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jib {
    from {
        image = "openjdk:21-jdk-slim"
        platforms {
            platform {
                architecture= "amd64"
                os = "linux"
            }
            platform {
                architecture = "arm64"
                os = "linux"
            }
        }
    }
    to {
        image = "${System.getenv("DOCKER_HOST")}/cqit/${rootProject.name}"
        tags = setOf("latest", "$version")
        auth {
            username = System.getenv("DOCKER_USERNAME")
            password = System.getenv("DOCKER_PASSWORD")
        }
    }
    container {
        mainClass = "nl.cqit.tools.buildnumbergenerator.BuildNumberGeneratorApplicationKt"
        ports = listOf("8080")
        jvmFlags = listOf("-Xms512m", "-Xmx1024m")
        creationTime = "USE_CURRENT_TIMESTAMP"
        extraClasspath
    }
}