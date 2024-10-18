plugins {
    id("java")
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.shadow)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.resource.factory.bukkit)
    alias(libs.plugins.resource.factory.velocity)
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = rootProject.libs.plugins.kotlin.jvm.get().pluginId)
    apply(plugin = rootProject.libs.plugins.kotlin.serialization.get().pluginId)
    apply(plugin = rootProject.libs.plugins.kotlin.kapt.get().pluginId)
    apply(plugin = rootProject.libs.plugins.jetbrains.compose.get().pluginId)
    apply(plugin = rootProject.libs.plugins.compose.compiler.get().pluginId)

    group = "ink.pmc.plutoproject"
    version = "1.2.0"

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

    dependencies {
        implementation(rootProject.libs.bundles.kotlin)
        implementation(rootProject.libs.bundles.mccoroutine)
        implementation(rootProject.libs.bundles.cloud)
        implementation(rootProject.libs.bundles.bytebuddy)
        implementation(rootProject.libs.bundles.nightconfig)
        implementation(rootProject.libs.bundles.protobuf)
        implementation(rootProject.libs.bundles.grpc)
        implementation(rootProject.libs.bundles.mongodb)
        implementation(rootProject.libs.okhttp)
        implementation(rootProject.libs.gson)
        implementation(rootProject.libs.catppuccin)
        implementation(rootProject.libs.caffeine)
        implementation(rootProject.libs.adventure.kt)
        implementation(rootProject.libs.bundles.koin)
        implementation(rootProject.libs.classgraph)
        implementation(provider { compose.runtime })
        implementation(provider { compose.runtimeSaveable })
        implementation(rootProject.libs.voyager.navigator)
        implementation(rootProject.libs.anvilGui)
        compileOnly(rootProject.libs.vault.api) {
            isTransitive = false
        }
    }
}

dependencies {
    implementation(project(":api"))
    implementation(project(":common"))
    implementation(project(":features-bukkit"))
    implementation(project(":features-velocity"))
    compileOnly(libs.paper.api)
    compileOnly(libs.velocity.api)
}

tasks.shadowJar {
    mergeServiceFiles()
    relocate("com.electronwill.nightconfig", "libs.nightconfig")
}

bukkitPluginYaml {
    main = "ink.pmc.plutoproject.BukkitPlugin"
    apiVersion = "1.20"
}

velocityPluginJson {
    main = "ink.pmc.plutoproject.VelocityPlugin"
}

tasks.jar {
    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang+yarn"
    }
}