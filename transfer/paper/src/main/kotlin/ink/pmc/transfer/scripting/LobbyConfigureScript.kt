package ink.pmc.transfer.scripting

import ink.pmc.transfer.PaperPlugin
import ink.pmc.utils.scripting.VelocityScriptCompilationConfiguration
import ink.pmc.utils.scripting.evalVelocityScript
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.SourceCode
import kotlin.script.experimental.api.constructorArgs
import kotlin.script.experimental.api.defaultImports

@KotlinScript(
    displayName = "Transfer lobby configure script",
    fileExtension = "lobby.kts",
    compilationConfiguration = VelocityScriptCompilationConfiguration::class
)
abstract class LobbyConfigureScript(scope: LobbyConfigureScope) : LobbyConfigureScope by scope

fun evalLobbyConfigureScript(source: SourceCode, scope: LobbyConfigureScope): LobbyConfigureScope {
    evalVelocityScript(source, compilation = {
        defaultImports.append("ink.pmc.transfer.scripting.*")
    }, evaluation = {
        constructorArgs(scope)
    }, classLoader = PaperPlugin::class.java.classLoader)
    return scope
}