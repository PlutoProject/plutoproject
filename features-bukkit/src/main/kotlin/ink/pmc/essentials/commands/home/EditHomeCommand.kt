package ink.pmc.essentials.commands.home

import ink.pmc.essentials.*
import ink.pmc.essentials.api.Essentials
import ink.pmc.essentials.api.home.Home
import ink.pmc.utils.PaperCtx
import ink.pmc.utils.chat.NON_PLAYER
import ink.pmc.utils.chat.isValidIdentifier
import ink.pmc.utils.chat.replace
import ink.pmc.utils.concurrent.submitAsync
import ink.pmc.utils.dsl.cloud.sender
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler
import org.incendo.cloud.parser.standard.StringParser

private val base = commandManager.commandBuilder("edithome")
    .permission("essentials.edithome")
    .argument(homes("home"))

private suspend fun PaperCtx.preprocess(): Pair<CommandSender, Home>? {
    val sender = this.sender.sender
    if (sender !is Player) {
        sender.sendMessage(NON_PLAYER)
        return null
    }
    val input = get<String>("home")
    val home = Essentials.homeManager.get(sender, input) ?: run {
        sender.sendMessage(COMMAND_HOME_NOT_EXISTED.replace("<name>", input))
        return null
    }
    return sender to home
}

private val preferred = base.literal("prefer")
    .suspendingHandler {
        val (sender, home) = it.preprocess() ?: return@suspendingHandler
        if (home.isPreferred) {
            sender.sendMessage(COMMAND_EDITHOME_ALREADY_PREFERRED.replace("<name>", home.name))
            return@suspendingHandler
        }
        submitAsync {
            home.setPreferred(true)
        }
        sender.sendMessage(COMMAND_EDITHOME_PREFER_SUCCEED.replace("<name>", home.name))
    }

private val star = base.literal("star")
    .suspendingHandler {
        val (sender, home) = it.preprocess() ?: return@suspendingHandler
        if (home.isStarred) {
            sender.sendMessage(COMMAND_EDITHOME_ALREADY_STARRED.replace("<name>", home.name))
            return@suspendingHandler
        }
        submitAsync {
            home.isStarred = true
            home.update()
        }
        sender.sendMessage(COMMAND_EDITHOME_STAR_SUCCEED.replace("<name>", home.name))
    }

private val rename = base.literal("rename")
    .required("new_name", StringParser.greedyStringParser())
    .suspendingHandler {
        val (sender, home) = it.preprocess() ?: return@suspendingHandler
        val newName = it.get<String>("new_name")
        if (!newName.isValidIdentifier) {
            sender.sendMessage(COMMAND_SETHOME_FAILED_NOT_VALID)
            return@suspendingHandler
        }
        if (newName.length > Essentials.homeManager.nameLengthLimit) {
            sender.sendMessage(COMMAND_SETHOME_FAILED_LENGTN_LIMIT)
            return@suspendingHandler
        }
        submitAsync {
            home.name = newName
            home.update()
        }
        sender.sendMessage(COMMAND_EDITHOME_RENAME_SUCCEED.replace("<new_name>", newName))
    }

private val move = base.literal("move")
    .suspendingHandler {
        val (sender, home) = it.preprocess() ?: return@suspendingHandler
        submitAsync {
            home.location = (sender as Player).location
            home.update()
        }
        sender.sendMessage(COMMAND_EDITHOME_MOVE_SUCCEED.replace("<name>", home.name))
    }

val editHomeCommand = listOf(preferred, star, rename, move)