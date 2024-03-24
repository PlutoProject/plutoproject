import xyz.jpenilla.runpaper.task.RunServer

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java-library")
    kotlin("plugin.serialization") version "1.9.21"
    kotlin("jvm") version "1.9.21"
    id("xyz.jpenilla.run-paper") version "2.2.2"
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
    this.version = "1.0.1"

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
        "com.electronwill.night-config:core:3.6.7",
        "com.electronwill.night-config:toml:3.6.0",
        "org.incendo:cloud-paper:2.0.0-beta.2",
        "org.incendo:cloud-velocity:2.0.0-beta.2"
    )

    val paper = listOf<String>()

    val velocity = listOf<String>()
    val project = this

    dependencies {
        common.forEach {
            if (project.name != "common-dependency-loader-velocity") {
                compileOnlyApi(it)
            } else {
                implementation(it)
            }
        }
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
        inputs.property("version", rootProject.version)
        inputs.property("api", bukkitAPIVersion)

        filesMatching("plugin.yml") {
            expand("version" to version, "api" to bukkitAPIVersion)
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
        if (it.name.startsWith("common-library-") || it.name.contains("velocity")) {
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

var debugMode = true

tasks.register("Paper") {
    runTest(tasks.runServer.get())
}

tasks.register("Folia") {
    runTest(tasks.named("runFolia").get())
}

tasks.register("copyJars") {
    group = "pluto develop testing"
    doLast {
        copyJars()
    }
}

tasks.register("markAsNonDebugMode") {
    group = "DONT RUN THESE TASKS MANUALLY"
    debugMode = false
}

tasks.register("runServerWithoutDebugMode") {
    group = "run paper"
    dependsOn(tasks.named("markAsNonDebugMode"), tasks.runServer)
}

tasks.register("runFoliaWithoutDebugMode") {
    group = "run paper"
    dependsOn(tasks.named("markAsNonDebugMode"), tasks.named("runFolia"))
}

// 不要用这个，会和 Paper 和 Folia 的测试共用文件夹
// Velocity 还是单独开一个服务端测试吧
/*tasks.register("Velocity") {
    runTest(tasks.runVelocity.get(), true)
}*/

runPaper.folia.registerTask()
runPaper.disablePluginJarDetection()

fun debugInitStep(task: Task) {
    task as RunServer

    val logs = mutableListOf<String>()

    logs.add(" ")
    logs.add("If you modified your code, you should rerun the shadowJar task to make the changes function properly.")
    logs.add("Maybe there will be a auto run task in the future, but now I haven't figure out how to implement it because the run-task plugin has some weird behaviors.")

    if (debugMode) {
        logs.add(" ")
        logs.add("By default, using Gradle plugin to run will be debug mode.")
        logs.add("In debug mode, some components which has behaviors related to the environment (such as connecting to a database) will be disabled.")
        logs.add("If you want to disable debug mode, you can run runServerWithoutDebugMode or runFoliaWithoutDebugMode.")
        logs.add(" ")
        task.jvmArgs(
            "-DPLUTO_DEBUG_MODE=TRUE"
        )
    } else {
        logs.add(" ")
        logs.add("You are not running debug mode, please ensure you need to do some things related to the environment (such as connecting to a database).")
        logs.add("Or it will make some components run into error.")
        logs.add(" ")
    }

    logs.forEach { println(it) }
}

val paperPlugins = runPaper.downloadPluginsSpec {
    url("https://ci.lucko.me/job/spark/401/artifact/spark-bukkit/build/libs/spark-1.10.60-bukkit.jar")
}

val foliaPlugins = runPaper.downloadPluginsSpec {
    url("https://ci.lucko.me/job/spark-folia/lastSuccessfulBuild/artifact/spark-bukkit/build/libs/spark-1.10.60-bukkit.jar")
}

tasks.runServer {
    doFirst {
        debugInitStep(this)
    }

    dependsOn(tasks.named("copyJars"))
    minecraftVersion("1.20.4")
    downloadPlugins.from(paperPlugins)
}

tasks.named("runFolia") {
    this as RunServer

    doFirst {
        debugInitStep(this)
    }

    dependsOn(tasks.named("copyJars"))
    downloadPlugins.from(foliaPlugins)
}