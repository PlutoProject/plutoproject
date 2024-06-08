package ink.pmc.utils.command

import com.velocitypowered.api.command.CommandSource
import org.bukkit.Bukkit
import org.incendo.cloud.component.CommandComponent
import org.incendo.cloud.parser.standard.StringParser
import org.incendo.cloud.suggestion.Suggestion
import java.util.concurrent.CompletableFuture

private fun onlinePlayers(argName: String, excludes: Array<out String>) =
    CommandComponent.builder<CommandSource, String>()
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
fun paperOptionalOnlinePlayersArgument(
    argName: String = "name",
    vararg excludes: String
): CommandComponent.Builder<CommandSource, String> {
    return onlinePlayers(argName, excludes).optional()
}

@Suppress("UNUSED")
fun paperRequiredOnlinePlayersArgument(
    argName: String = "name",
    vararg excludes: String
): CommandComponent.Builder<CommandSource, String> {
    return onlinePlayers(argName, excludes).required()
}