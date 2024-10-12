import org.jetbrains.kotlin.gradle.plugin.extraProperties
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    id("java")
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.shadow)
    alias(libs.plugins.runpaper)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.paperweight.userdev)
    alias(libs.plugins.resource.factory.bukkit)
    alias(libs.plugins.resource.factory.velocity)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
}

val paperDevBundleVer = "1.21-R0.1-SNAPSHOT"
extra["paperDevBundleVer"] = paperDevBundleVer

fun kotlin(s: String): String {
    return "org.jetbrains.kotlin.$s"
}

val bukkitApiVersion by extra("1.21")
val root = project

fun Project.ensureParent(): Boolean {
    return this.parent == rootProject
}

fun <T> tryOrNull(block: () -> T): T? {
    return try {
        block()
    } catch (e: Exception) {
        null
    }
}

fun Project.dependOnOtherModule(name: String, impl: Boolean = false) {
    val par = parent?.name
    val module = tryOrNull { project(":$par:$name") } ?: return

    dependencies {
        if (impl) {
            implementation(module)
        } else {
            compileOnly(module)
        }
    }
}

fun Project.dependOnApi() {
    dependOnOtherModule("api")
}

fun Project.dependOnProto() {
    val par = parent?.name
    val proto = tryOrNull { project(":$par:proto") } ?: return

    dependencies {
        protobuf(proto)
    }
}

fun Project.dependOnShared() {
    dependOnOtherModule("shared")
}

fun Project.configureRelocate() {
    tasks.shadowJar {
        relocate("com.electronwill.nightconfig", "ink.pmc.libs.nightconfig")
    }
}

fun Project.initDevEnv() {
    dependencies {
        subprojects {
            when (project.name) {
                "shared" -> afterEvaluate {
                    applySharedDevEnv()
                    implementationWithEnv(project)
                    project(project.path) {
                        dependOnApi()
                        dependOnProto()
                    }
                }

                "paper" -> {
                    configurePaperPlugin()
                    afterEvaluate {
                        project(project.path) {
                            enablePaperDevEnv()
                            dependOnApi()
                            dependOnProto()
                            dependOnShared()
                        }
                        implementationWithEnv(project)
                    }
                }

                "velocity" -> {
                    configureVelocityPlugin()
                    afterEvaluate {
                        implementationWithEnv(project)
                        project(project.path) {
                            enableVelocityDevEnv()
                            dependOnApi()
                            dependOnProto()
                            dependOnShared()
                        }
                    }
                }

                "api" -> afterEvaluate {
                    implementationWithEnv(project)
                    project(project.path) {
                        enableApiDevEnv()
                    }
                }

                "proto" -> {
                    protobuf(project)
                }
            }
        }
    }

    configureRelocate()
}

fun Project.configurePaperweight() {
    apply {
        plugin("io.papermc.paperweight.userdev")
    }

    paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
}

fun Project.packageName(): String {
    if (!ensureParent() && parent != null) {
        return "${parent!!.packageName()}.$name"
    }

    return "ink.pmc.${name.lowercase().replace("-", "")}"
}

fun Project.configurePaperPlugin() {
    apply(plugin = "xyz.jpenilla.resource-factory-bukkit-convention")

    afterEvaluate {
        bukkitPluginYaml {
            main = "${parent?.group}.PaperPlugin"
            name = parent?.name
            apiVersion = bukkitApiVersion

            if (name.get().contains("dependency-loader")) {
                return@bukkitPluginYaml
            }

            if (!name.get().contains("dependency-loader")) {
                depend.add("dependency-loader")
            }

            if (!name.get().contains("utils")) {
                depend.add("utils")
            }
        }
    }
}

fun Project.configureVelocityPlugin() {
    apply(plugin = "xyz.jpenilla.resource-factory-velocity-convention")

    afterEvaluate {
        velocityPluginJson {
            main = "${parent?.group}.VelocityPlugin"
            id = parent?.name
            name = parent?.name

            if (name.get().contains("dependency-loader")) {
                return@velocityPluginJson
            }

            if (!name.get().contains("dependency-loader")) {
                dependency("dependency-loader")
            }

            if (!name.get().contains("utils")) {
                dependency("utils")
            }
        }
    }
}

fun Project.extraOrNull(key: String): Any? {
    if (!this.extraProperties.has(key)) {
        return null
    }

    return this.extra[key]
}

fun Project.applySharedDevEnv() {
    dependencies {
        compileOnly(root.libs.velocity.api)
        compileOnly(root.libs.paper.api)
    }
}

val paperDevEnvProp = "paperDevEnv"
val velocityDevEnvProp = "velocityDevEnv"
val apiDevEnvProp = "apiDevEnv"

fun Project.enablePaperDevEnv() {
    extra[paperDevEnvProp] = true
}

fun Project.enableVelocityDevEnv() {
    extra[velocityDevEnvProp] = true
}

fun Project.enableApiDevEnv() {
    extra[apiDevEnvProp] = true
}

fun Project.configurePaperDevEnv() {
    if (extraOrNull(paperDevEnvProp) != true) {
        return
    }

    configurePaperweight()

    configurations.create("obf").extendsFrom(
        // configurations.reobf.get(),
        configurations.apiElements.get(),
        configurations.runtimeElements.get()
    )

    dependencies {
        paperweight.paperDevBundle(paperDevBundleVer)
    }
}

fun DependencyHandlerScope.implementationWithEnv(dep: Project) {
    if (dep.extraOrNull(paperDevEnvProp) == true) {
        implementation(project(path = dep.path, configuration = "obf"))
    }

    implementation(dep)
}

fun Project.configureVelocityDevEnv() {
    if (extraOrNull(velocityDevEnvProp) != true) {
        return
    }

    dependencies {
        compileOnly(root.libs.velocity.api)
        compileOnly(root.libs.velocity)
        kapt(root.libs.velocity.api)
    }
}

fun Project.configureApiDevEnv() {
    if (extraOrNull(apiDevEnvProp) != true) {
        return
    }

    dependencies {
        compileOnly(root.libs.velocity.api)
        compileOnly(root.libs.paper.api)
    }
}

fun Project.applyProtobuf() {
    protobuf {
        protoc {
            artifact = root.libs.protoc.asProvider().get().toString()
        }
        plugins {
            create("grpc") {
                artifact = root.libs.protoc.gen.grpc.java.get().toString()
            }
            create("grpckt") {
                artifact = root.libs.protoc.gen.grpc.kotlin.get().toString() + ":jdk8@jar"
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

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = rootProject.libs.plugins.kotlin.jvm.get().pluginId)
    apply(plugin = rootProject.libs.plugins.kotlin.serialization.get().pluginId)
    apply(plugin = rootProject.libs.plugins.kotlin.kapt.get().pluginId)
    apply(plugin = rootProject.libs.plugins.shadow.get().pluginId)
    apply(plugin = rootProject.libs.plugins.protobuf.get().pluginId)
    apply(plugin = rootProject.libs.plugins.jetbrains.compose.get().pluginId)
    apply(plugin = rootProject.libs.plugins.compose.compiler.get().pluginId)

    this.group = packageName()
    this.version = "1.2.0"

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    kotlin {
        jvmToolchain(21)
    }

    repositories {
        mavenCentral()
        mavenLocal()
        google()
        maven(uri("https://jitpack.io"))
        maven(uri("https://oss.sonatype.org/content/repositories/snapshots/"))
        maven(uri("https://repo.papermc.io/repository/maven-public/"))
        maven(uri("https://maven.nostal.ink/repository/maven-public"))
        maven(uri("https://maven.playpro.com/"))
        maven(uri("https://repo.opencollab.dev/main/"))
        maven(uri("https://repo.dmulloy2.net/repository/public/"))
        maven(uri("https://mvn.exceptionflug.de/repository/exceptionflug-public/"))
        maven(uri("https://repo.xenondevs.xyz/releases"))
        maven(uri("https://repo.codemc.io/repository/maven-snapshots/"))
        maven(uri("https://repo.william278.net/releases"))
    }

    fun DependencyHandlerScope.dep(dep: Provider<*>, dependencyConfiguration: Action<ExternalModuleDependency> = Action {  }) {
        if (project.name.contains("dependency-loader")) {
            implementation(dep, dependencyConfiguration)
            return
        }
        compileOnly(dep, dependencyConfiguration)
    }

    dependencies {
        dep(rootProject.libs.bundles.kotlin)
        dep(rootProject.libs.bundles.mccoroutine)
        dep(rootProject.libs.bundles.cloud)
        dep(rootProject.libs.bundles.bytebuddy)
        dep(rootProject.libs.bundles.nightconfig)
        dep(rootProject.libs.bundles.protobuf)
        dep(rootProject.libs.bundles.grpc)
        dep(rootProject.libs.bundles.mongodb)
        dep(rootProject.libs.okhttp)
        dep(rootProject.libs.gson)
        dep(rootProject.libs.catppuccin)
        dep(rootProject.libs.caffeine)
        dep(rootProject.libs.adventure.kt)
        dep(rootProject.libs.bundles.koin)
        dep(rootProject.libs.classgraph)
        dep(provider { compose.runtime })
        dep(provider { compose.runtimeSaveable })
        dep(rootProject.libs.bundles.voyager)
        dep(rootProject.libs.anvilGui)
        compileOnly(rootProject.libs.vault.api) {
            isTransitive = false
        }
    }

    tasks.shadowJar {
        if (!ensureParent()) {
            return@shadowJar
        }

        archiveClassifier = ""
        onlyIf { project != rootProject && !project.name.startsWith("common-library-") }
        destinationDirectory.set(file("$rootDir/build-outputs"))
    }

    tasks.compileJava {
        options.encoding = "UTF-8"
    }

    tasks.jar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang+yarn"
        }
    }

    configurations.all {
        resolutionStrategy {
            force("com.google.inject:guice:4.2.2")
        }
    }

    applyProtobuf()
}

tasks.shadowJar {
    doFirst { clearOutputsDir() }
}

subprojects {
    if (!ensureParent()) {
        return@subprojects
    }

    initDevEnv()
}

/*
* 需要在 rootProject 中设置。
* 否则下方拓展函数无法正常使用。
* */
project.enablePaperDevEnv()
bukkitPluginYaml {
    main = packageName()
}
velocityPluginJson {
    main = packageName()
}

allprojects {
    fun afterEvaluateIfNeeded(block: () -> Unit) {
        if (project != rootProject) {
            afterEvaluate {
                block()
            }
            return
        }

        block()
    }

    afterEvaluateIfNeeded {
        configurePaperDevEnv()
        configureVelocityDevEnv()
        configureApiDevEnv()
    }
}

val String.trimmed: String
    get() {
        return replace(
            substring(
                lastIndexOf('-'),
                lastIndexOf('.')
            ),
            ""
        )
    }

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