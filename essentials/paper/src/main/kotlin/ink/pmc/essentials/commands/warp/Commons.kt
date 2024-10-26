package ink.pmc.essentials.commands.warp

import ink.pmc.framework.utils.concurrent.submitAsync
import io.papermc.paper.command.brigadier.CommandSourceStack
import kotlinx.coroutines.future.asCompletableFuture
import org.bukkit.entity.Player
import org.incendo.cloud.component.CommandComponent
import org.incendo.cloud.parser.standard.StringParser
import org.incendo.cloud.suggestion.Suggestion
import java.util.concurrent.CompletableFuture

internal fun warps(argName: String) =
    CommandComponent.builder<CommandSourceStack, String>()
        .suggestionProvider { ctx, _ ->
            val manager = Essentials.warpManager
            val sender = ctx.sender().sender
            if (sender is Player) {
                submitAsync<List<Suggestion>> {
                    manager.list().map {
                        val name = it.name
                        val alias = it.alias
                        if (alias == null) {
                            Suggestion.suggestion(name)
                        } else {
                            Suggestion.suggestion("$name-$alias")
                        }
                    }
                }.asCompletableFuture()
            } else {
                CompletableFuture.completedFuture(listOf())
            }
        }
        .parser(StringParser.greedyStringParser())
        .name(argName)

internal fun warpsWithoutAlias(argName: String) =
    CommandComponent.builder<CommandSourceStack, String>()
        .suggestionProvider { ctx, _ ->
            val manager = Essentials.warpManager
            val sender = ctx.sender().sender
            if (sender is Player) {
                submitAsync<List<Suggestion>> {
                    manager.list().map {
                        val name = it.name
                        Suggestion.suggestion(name)
                    }
                }.asCompletableFuture()
            } else {
                CompletableFuture.completedFuture(listOf())
            }
        }
        .parser(StringParser.quotedStringParser())
        .name(argName)