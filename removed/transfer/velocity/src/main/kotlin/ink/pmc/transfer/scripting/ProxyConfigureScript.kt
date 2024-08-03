package ink.pmc.transfer.scripting

import ink.pmc.transfer.VelocityPlugin
import ink.pmc.utils.scripting.PaperScriptCompilationConfiguration
import ink.pmc.utils.scripting.VelocityScriptCompilationConfiguration
import ink.pmc.utils.scripting.evalCustomScript
import ink.pmc.utils.scripting.importClasspath
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.SourceCode
import kotlin.script.experimental.api.constructorArgs
import kotlin.script.experimental.api.defaultImports

@KotlinScript(
    displayName = "Transfer proxy configure script",
    fileExtension = "proxy.kts",
    compilationConfiguration = VelocityScriptCompilationConfiguration::class
)
abstract class ProxyConfigureScript(scope: ProxyConfigureScope) : ProxyConfigureScope by scope

fun evalProxyConfigureScript(source: SourceCode, scope: ProxyConfigureScope): ProxyConfigureScope {
    evalCustomScript<ProxyConfigureScript>(source, compilation = {
        importClasspath(PaperScriptCompilationConfiguration::class.java.classLoader)
        defaultImports.append("ink.pmc.transfer.scripting.*")
    }, evaluation = {
        constructorArgs(scope)
    }, classLoader = VelocityPlugin::class.java.classLoader)
    return scope
}