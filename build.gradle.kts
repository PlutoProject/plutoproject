import org.jetbrains.kotlin.ir.backend.js.compile
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    id("java")
    id("java-library")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.shadow)
    alias(libs.plugins.runpaper)
    alias(libs.plugins.protobuf)
}

fun kotlin(s: String): String {
    return "org.jetbrains.kotlin.$s"
}

val bukkitApiVersion by extra("1.20")

allprojects {

    apply {
        plugin("java")
        plugin("java-library")
        plugin(kotlin("jvm"))
        plugin(kotlin("plugin.serialization"))
        plugin(kotlin("kapt"))
        plugin("com.github.johnrengelman.shadow")
        plugin("com.google.protobuf")
    }

    this.group = "ink.pmc.common"
    this.version = "1.1.0"

    repositories {
        mavenCentral()
        mavenLocal()
        maven(uri("https://oss.sonatype.org/content/repositories/snapshots/"))
        maven(uri("https://repo.papermc.io/repository/maven-public/"))
        maven(uri("https://maven.nostal.ink/repository/maven-public"))
        maven(uri("https://repo.opencollab.dev/main/"))
        maven(uri("https://repo.dmulloy2.net/repository/public/"))
        maven(uri("https://mvn.exceptionflug.de/repository/exceptionflug-public/"))
    }

    fun DependencyHandlerScope.dep(dep: Provider<*>) {
        if (project.name.contains("dependency-loader")) {
            implementation(dep)
            return
        }
        compileOnly(dep)
    }

    dependencies {
        dep(rootProject.libs.bundles.kotlin)
        dep(rootProject.libs.bundles.mccoroutine)
        dep(rootProject.libs.bundles.cloud)
        dep(rootProject.libs.bundles.bytebuddy)
        dep(rootProject.libs.bundles.nightconfig)
        dep(rootProject.libs.bundles.protobuf)
        dep(rootProject.libs.bundles.grpc)
        dep(rootProject.libs.paper.api)
        dep(rootProject.libs.velocity.api)
        dep(rootProject.libs.okhttp)
        dep(rootProject.libs.gson)
        dep(rootProject.libs.mongodb)
        dep(rootProject.libs.catppuccin)
        dep(rootProject.libs.netty)
        dep(rootProject.libs.jsoup)
    }

    tasks.processResources {
        inputs.property("version", rootProject.version)
        inputs.property("api", bukkitApiVersion)

        filesMatching("plugin.yml") {
            expand("version" to version, "api" to bukkitApiVersion)
        }
    }

    tasks.shadowJar {
        clearOutputsDir()
        archiveClassifier = ""
        onlyIf { project != rootProject && !project.name.startsWith("common-library-") }
        destinationDirectory.set(file("$rootDir/build-outputs"))
    }

    protobuf {
        protoc {
            artifact = rootProject.libs.protoc.asProvider().get().toString()
        }
        plugins {
            create("grpc") {
                artifact = rootProject.libs.protoc.gen.grpc.java.get().toString()
            }
            create("grpckt") {
                artifact = rootProject.libs.protoc.gen.grpc.kotlin.get().toString() + ":jdk8@jar"
            }
        }
        generateProtoTasks {
            all().forEach {
                it.plugins {
                    create("grpc")
                    create("grpckt")
                }
                it.builtins {
                    create("kotlin")
                }
            }
        }
    }
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

runPaper.folia.registerTask()
runPaper.disablePluginJarDetection()

fun debugInitStep(task: Task) {
    task as RunServer

    val logs = mutableListOf<String>()

    logs.add(" ")
    logs.add("Some component's feature will be disabled due to the debug plugin can't support Leaves-PMC.")
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
    url("https://github.com/dmulloy2/ProtocolLib/releases/download/5.2.0/ProtocolLib.jar")
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