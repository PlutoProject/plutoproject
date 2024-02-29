plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("plugin.serialization") version "1.9.21"
    kotlin("jvm") version "1.9.21"
}

fun kotlinDep(s: String): String {
    return "org.jetbrains.kotlin.$s"
}

allprojects {

    apply {
        plugin("java")
        plugin("com.github.johnrengelman.shadow")
        plugin(kotlinDep("jvm"))
        plugin(kotlinDep("plugin.serialization"))
    }

    this.group = "ink.pmc.starlib"
    this.version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        maven {
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
    }

    dependencies {
        // api("org.jetbrains.kotlinx:kotlinx-serialization-json")
        // api("org.jetbrains.kotlinx:kotlinx-serialization-toml")
        // api("org.jetbrains.kotlinx:kotlinx-serialization-hocon")
        api("com.squareup.okhttp3:okhttp:5.0.0-alpha.12")
        api("com.google.code.gson:gson:2.10.1")
        api("org.mongojack:mongojack:4.8.2")
        api("org.mongodb:mongodb-driver-sync:4.11.1")
        api("com.github.ben-manes.caffeine:caffeine:3.1.8")

        compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    }
}