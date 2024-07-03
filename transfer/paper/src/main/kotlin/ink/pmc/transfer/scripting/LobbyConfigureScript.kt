package ink.pmc.transfer.scripting

import ink.pmc.transfer.PaperPlugin
import ink.pmc.utils.scripting.PaperScriptCompilationConfiguration
import ink.pmc.utils.scripting.evalCustomScript
import ink.pmc.utils.scripting.importClasspath
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.SourceCode
import kotlin.script.experimental.api.constructorArgs
import kotlin.script.experimental.api.defaultImports

@KotlinScript(
    displayName = "Transfer lobby configure script",
    fileExtension = "lobby.kts",
    compilationConfiguration = PaperScriptCompilationConfiguration::class
)
abstract class LobbyConfigureScript(scope: LobbyConfigureScope) : LobbyConfigureScope by scope

fun evalLobbyConfigureScript(source: SourceCode, scope: LobbyConfigureScope): LobbyConfigureScope {
    evalCustomScript<LobbyConfigureScript>(source, compilation = {
        importClasspath(PaperScriptCompilationConfiguration::class.java.classLoader)
        defaultImports.append("ink.pmc.transfer.scripting.*")
        defaultImports.append("net.kyori.adventure.key.*")
    }, evaluation = {
        constructorArgs(scope)
    }, classLoader = PaperPlugin::class.java.classLoader)
    return scope
}