package ink.pmc.utils.scripting

import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.*
import kotlin.script.experimental.dependencies.DependsOn
import kotlin.script.experimental.dependencies.Repository
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm

private val velocityImports = listOf(
    "com.velocitypowered.api.*",
    "com.velocitypowered.api.command.*",
    "com.velocitypowered.api.event.*",
    "com.velocitypowered.api.event.annotation.*",
    "com.velocitypowered.api.event.command.*",
    "com.velocitypowered.api.event.connection.*",
    "com.velocitypowered.api.event.permission.*",
    "com.velocitypowered.api.event.player.*",
    "com.velocitypowered.api.event.player.configuration.*",
    "com.velocitypowered.api.event.proxy.*",
    "com.velocitypowered.api.event.query.*",
    "com.velocitypowered.api.network.*",
    "com.velocitypowered.api.permission.*",
    "com.velocitypowered.api.plugin.*",
    "com.velocitypowered.api.plugin.annotation.*",
    "com.velocitypowered.api.plugin.ap.*",
    "com.velocitypowered.api.plugin.meta.*",
    "com.velocitypowered.api.proxy.*",
    "com.velocitypowered.api.proxy.config.*",
    "com.velocitypowered.api.proxy.crypto.*",
    "com.velocitypowered.api.proxy.server.*",
    "com.velocitypowered.api.scheduler.*",
    "com.velocitypowered.api.util.*",
)

@KotlinScript(
    displayName = "Velocity script",
    fileExtension = "velocity.kts",
    compilationConfiguration = VelocityScriptCompilationConfiguration::class
)
abstract class VelocityScript

object VelocityScriptCompilationConfiguration : ScriptCompilationConfiguration({

    jvm {
        dependenciesFromCurrentContext(
            wholeClasspath = true
        )
    }

    compilerOptions.append("-Xadd-modules=ALL-MODULE-PATH")

    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }

    refineConfiguration {
        onAnnotations(DependsOn::class, Repository::class, handler = ::configureMavenDepsOnAnnotations)
    }

    defaultImports(baseImports + velocityImports)

})