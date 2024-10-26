package ink.pmc.essentials.commands.warp

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.text
import ink.pmc.essentials.COMMAND_PREFERRED_SPAWN_FAILED_ALREADY
import ink.pmc.essentials.COMMAND_PREFERRED_SPAWN_SUCCEED
import ink.pmc.essentials.COMMAND_SPAWN_NOT_EXISTED
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.essentials.commandManager
import ink.pmc.essentials.commands.parseWarp
import ink.pmc.essentials.commands.parseWarpName
import ink.pmc.framework.utils.chat.NON_PLAYER
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.concurrent.submitAsync
import ink.pmc.framework.utils.dsl.cloud.sender
import ink.pmc.framework.utils.visual.mochaSubtext0
import ink.pmc.framework.utils.visual.mochaText
import io.papermc.paper.command.brigadier.CommandSourceStack
import kotlinx.coroutines.future.asCompletableFuture
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.incendo.cloud.component.CommandComponent
import org.incendo.cloud.context.CommandContext
import org.incendo.cloud.context.CommandInput
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler
import org.incendo.cloud.parser.standard.StringParser
import org.incendo.cloud.suggestion.Suggestion
import org.incendo.cloud.suggestion.SuggestionProvider
import java.util.concurrent.CompletableFuture

private object SpawnSuggestion : SuggestionProvider<CommandSourceStack> {
    override fun suggestionsFuture(
        context: CommandContext<CommandSourceStack>,
        input: CommandInput
    ): CompletableFuture<List<Suggestion>> {
        return submitAsync<List<Suggestion>> {
            val spawns = WarpManager.listSpawns()
            spawns.map { Suggestion.suggestion(it.name) }
        }.asCompletableFuture()
    }
}

val defaultSpawnCommand = commandManager.commandBuilder("preferredspawn")
    .permission("essentials.defaultspawn")
    .argument(
        CommandComponent.builder<CommandSourceStack, String>()
            .suggestionProvider(SpawnSuggestion)
            .parser(StringParser.quotedStringParser())
            .required()
            .name("warp")
            .build()
    )
    .suspendingHandler {
        val sender = it.sender.sender
        if (sender !is Player) {
            sender.sendMessage(NON_PLAYER)
            return@suspendingHandler
        }
        val input = it.get<String>("warp")
        val name = parseWarpName(input)
        val spawn = parseWarp(input) ?: run {
            sender.sendMessage(COMMAND_SPAWN_NOT_EXISTED.replace("<name>", name))
            return@suspendingHandler
        }
        val current = WarpManager.getPreferredSpawn(sender)

        if (current?.name == spawn.name) {
            sender.sendMessage(
                COMMAND_PREFERRED_SPAWN_FAILED_ALREADY.replace("<name>", if (spawn.alias != null) component {
                    text("${spawn.alias} ") with mochaText
                    text("(${spawn.name})") with mochaSubtext0
                } else Component.text(spawn.name)))
            return@suspendingHandler
        }

        WarpManager.setPreferredSpawn(sender, spawn)
        sender.sendMessage(
            COMMAND_PREFERRED_SPAWN_SUCCEED.replace("<name>", if (spawn.alias != null) component {
                text("${spawn.alias} ") with mochaText
                text("(${spawn.name})") with mochaSubtext0
            } else Component.text(spawn.name)))
    }