package ink.pmc.utils.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.incendo.cloud.component.CommandComponent
import org.incendo.cloud.parser.standard.StringParser
import org.incendo.cloud.suggestion.Suggestion
import java.util.concurrent.CompletableFuture

private fun onlinePlayers(argName: String, excludes: Array<out String>) =
    CommandComponent.builder<CommandSender, String>()
        .suggestionProvider { _, _ ->
            CompletableFuture.completedFuture(
                Bukkit.getOnlinePlayers()
                    .map { it.name }
                    .filter { !excludes.map { exc -> exc.lowercase() }.contains(it.lowercase()) }
                    .map { Suggestion.suggestion(it) }
            )
        }
        .parser(StringParser.stringParser())
        .name(argName)

@Suppress("UNUSED")
fun bukkitOptionalOnlinePlayersArgument(
    argName: String = "name",
    vararg excludes: String
): CommandComponent.Builder<CommandSender, String> {
    return onlinePlayers(argName, excludes).optional()
}

@Suppress("UNUSED")
fun bukkitRequiredOnlinePlayersArgument(
    argName: String = "name",
    vararg excludes: String
): CommandComponent.Builder<CommandSender, String> {
    return onlinePlayers(argName, excludes).required()
}