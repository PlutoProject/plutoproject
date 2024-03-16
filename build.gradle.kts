plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java-library")
    kotlin("plugin.serialization") version "1.9.21"
    kotlin("jvm") version "1.9.21"
    id("xyz.jpenilla.run-paper") version "2.2.2"
    id("xyz.jpenilla.run-velocity") version "2.2.2"
    kotlin("kapt") version "1.9.23"
}

fun kotlinDep(s: String): String {
    return "org.jetbrains.kotlin.$s"
}

allprojects {

    apply {
        plugin("java")
        plugin("com.github.johnrengelman.shadow")
        plugin("java-library")
        plugin(kotlinDep("jvm"))
        plugin(kotlinDep("plugin.serialization"))
        plugin(kotlinDep("kapt"))
    }

    val bukkitAPIVersion by extra("1.20")

    this.group = "ink.pmc.common"
    this.version = "1.0.0"

    repositories {
        mavenCentral()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        maven {
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
    }

    val common = listOf(
        "com.squareup.okhttp3:okhttp:5.0.0-alpha.12",
        "com.google.code.gson:gson:2.10.1",
        "org.mongojack:mongojack:4.8.2",
        "org.mongodb:mongodb-driver-sync:4.11.1",
        "com.github.ben-manes.caffeine:caffeine:3.1.8",
        "com.catppuccin:catppuccin-palette:1.0.0",
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0",
        "com.electronwill.night-config:yaml:3.6.0"
    )

    val paper = listOf(
        "org.incendo:cloud-paper:2.0.0-beta.2"
    )

    val velocity = listOf<String>()

    dependencies {
        common.forEach { compileOnlyApi(it) }
    }

    if (!this.name.contains("velocity")) {
        dependencies {
            paper.forEach { compileOnlyApi(it) }
            compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
        }
    } else if (this.name.contains("velocity")) {
        dependencies {
            velocity.forEach { compileOnlyApi(it) }
            compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
            kapt("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
        }
    }

    tasks.processResources {
        filesMatching("plugin.yml") {
            expand("version" to rootProject.version, "api" to bukkitAPIVersion)
        }
    }

    tasks.shadowJar {
        clearOutputsDir()
        archiveClassifier = ""
        onlyIf { project != rootProject && !project.name.startsWith("common-library-") }
        destinationDirectory.set(file("$rootDir/build-outputs"))
    }
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    kapt("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
}

fun trim(jarName: String): String {
    return jarName.replace(
        jarName.substring(
            jarName.lastIndexOf('-'),
            jarName.lastIndexOf('.')
        ),
        ""
    )
}

val velocityShared = listOf("common-member")

val String.trimmed: String
    get() = trim(this)

fun copyJars() {
    val outputsDir = file("$rootDir/build-outputs")

    outputsDir.listFiles()!!.forEach {
        if (it.name.startsWith("common-library-")) {
            return@forEach
        }

        val folder = file("$rootDir/run/plugins/")

        if (!folder.exists()) {
            folder.mkdirs()
        }

        val target = File(
            folder,
            it.name.trimmed
        )

        if (target.exists()) {
            target.delete()
        }

        it.copyTo(target)
    }
}

fun clearOutputsDir() {
    val dir = file("$rootDir/build-outputs")

    if (!dir.exists()) {
        dir.mkdirs()
    }

    file(file("$rootDir/build-outputs")).listFiles()!!.forEach {
        it.delete()
    }
}

fun Task.runTest(task: Task) {
    group = "pluto develop testing"
    dependsOn(allprojects.map { it.tasks.named("shadowJar") })

    doLast {
        copyJars()
        task.actions.forEach { it.execute(task) }
    }
}

tasks.register("Paper") {
    runTest(tasks.runServer.get())
}

tasks.register("Folia") {
    runTest(tasks.named("runFolia").get())
}

// 不要用这个，会和 Paper 和 Folia 的测试共用文件夹
// Velocity 还是单独开一个服务端测试吧
/*tasks.register("Velocity") {
    runTest(tasks.runVelocity.get(), true)
}*/

runPaper.folia.registerTask()
runPaper.disablePluginJarDetection()

tasks.runServer.configure {
    minecraftVersion("1.20.4")
}