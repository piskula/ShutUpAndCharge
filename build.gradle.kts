import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.1.10"
}

allprojects {
    tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_21.toString()
        targetCompatibility = JavaVersion.VERSION_21.toString()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = JavaVersion.VERSION_21.toString()
        }
    }
}

subprojects {
    group = "sk.momosilabs.suac"
    version = "1.0.28-SNAPSHOT"

    apply {
        plugin("io.spring.dependency-management")
        plugin("org.jetbrains.kotlin.jvm")
    }

    repositories {
        mavenCentral()
    }
}
repositories {
  mavenCentral()
}

dependencies {
    platform("org.springframework.boot:spring-boot-dependencies:3.4.2.RELEASE")
}
