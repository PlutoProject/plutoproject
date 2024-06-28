package ink.pmc.utils.scripting

import kotlinx.coroutines.runBlocking
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.*
import kotlin.script.experimental.dependencies.*
import kotlin.script.experimental.dependencies.maven.MavenDependenciesResolver
import kotlin.script.experimental.jvm.JvmDependency
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm

val baseImports = listOf(
    // Kotlin scripting
    "kotlin.script.experimental.dependencies.DependsOn",
    "kotlin.script.experimental.dependencies.Repository",

    // Kotlin stdlib
    "kotlin.time.*",
    "kotlin.math.*",

    // Java utils
    "java.util.*",
    "java.util.concurrent.*",
    "java.io.*",

    // Kotlin coroutine
    "kotlinx.coroutines.*",
    "kotlinx.coroutines.flow.*",
    "kotlinx.coroutines.channels.*",
    "kotlinx.coroutines.selects.*",

    // PlutoProject utils
    "ink.pmc.utils.*",
    "ink.pmc.utils.bedrock.*",
    "ink.pmc.utils.chat.*",
    "ink.pmc.utils.command.*",
    "ink.pmc.utils.concurrent.*",
    "ink.pmc.utils.platform.*",
    "ink.pmc.utils.player.*",
    "ink.pmc.utils.storage.*",
    "ink.pmc.utils.world.*",
)

/*
* Copied from official Kotlin Scripting example.
* */

private val scriptResolver = CompoundDependenciesResolver(FileSystemDependenciesResolver(), MavenDependenciesResolver())

fun configureMavenDepsOnAnnotations(context: ScriptConfigurationRefinementContext): ResultWithDiagnostics<ScriptCompilationConfiguration> {
    val annotations = context.collectedData?.get(ScriptCollectedData.collectedAnnotations)?.takeIf { it.isNotEmpty() }
        ?: return context.compilationConfiguration.asSuccess()
    return runBlocking {
        scriptResolver.resolveFromScriptSourceAnnotations(annotations)
    }.onSuccess {
        context.compilationConfiguration.with {
            dependencies.append(JvmDependency(it))
        }.asSuccess()
    }
}

@KotlinScript(
    displayName = "Base script",
    fileExtension = "base.kts",
    compilationConfiguration = BaseScriptCompilationConfiguration::class
)
abstract class BaseScript

object BaseScriptCompilationConfiguration : ScriptCompilationConfiguration({

    jvm {
        dependenciesFromClassContext(
            BaseScriptCompilationConfiguration::class,
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

    defaultImports(baseImports)

})