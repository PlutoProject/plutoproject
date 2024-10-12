package ink.pmc.essentials.commands.home

import ink.pmc.essentials.api.Essentials
import ink.pmc.utils.concurrent.submitAsync
import io.papermc.paper.command.brigadier.CommandSourceStack
import kotlinx.coroutines.future.asCompletableFuture
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.component.CommandComponent
import org.incendo.cloud.parser.standard.StringParser
import org.incendo.cloud.suggestion.Suggestion
import java.util.concurrent.CompletableFuture

internal fun homes(argName: String) =
    CommandComponent.builder<CommandSender, String>()
        .suggestionProvider { ctx, _ ->
            val manager = Essentials.homeManager
            val sender = ctx.sender()
            if (sender is Player) {
                submitAsync<List<Suggestion>> {
                    manager.list(sender).map { Suggestion.suggestion(it.name) }
                }.asCompletableFuture()
            } else {
                CompletableFuture.completedFuture(listOf())
            }
        }
        .parser(StringParser.quotedStringParser())
        .name(argName)