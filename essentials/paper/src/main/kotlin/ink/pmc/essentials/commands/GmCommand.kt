package ink.pmc.essentials.commands

import ink.pmc.essentials.*
import ink.pmc.framework.utils.chat.NON_PLAYER
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.command.annotation.Command
import ink.pmc.framework.utils.concurrent.sync
import ink.pmc.framework.utils.dsl.cloud.alias
import ink.pmc.framework.utils.dsl.cloud.invoke
import ink.pmc.framework.utils.dsl.cloud.sender
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.incendo.cloud.bukkit.parser.PlayerParser
import org.incendo.cloud.context.CommandContext
import kotlin.jvm.optionals.getOrNull

@Command("gm")
@Suppress("UNUSED")
fun Cm.gm(aliases: Array<String>) {
    this("gm", *aliases) {
        ("survival" alias "s" alias "0") {
            permission("essentials.gm.survival")
            optional("player", PlayerParser.playerParser())
            handler {
                gmHandler(GameMode.SURVIVAL)
            }
        }

        ("creative" alias "c" alias "1") {
            permission("essentials.gm.creative")
            optional("player", PlayerParser.playerParser())
            handler {
                gmHandler(GameMode.CREATIVE)
            }
        }

        ("adventure" alias "a" alias "2") {
            permission("essentials.gm.adventure")
            optional("player", PlayerParser.playerParser())
            handler {
                gmHandler(GameMode.ADVENTURE)
            }
        }

        ("spectator" alias "sp" alias "3") {
            permission("essentials.gm.spectator")
            optional("player", PlayerParser.playerParser())
            handler {
                gmHandler(GameMode.SPECTATOR)
            }
        }
    }
}

@Command("gms")
@Suppress("UNUSED")
fun Cm.gms(aliases: Array<String>) {
    this("gms", *aliases) {
        permission("essentials.gm.survival")
        optional("player", PlayerParser.playerParser())
        handler {
            gmHandler(GameMode.SURVIVAL)
        }
    }
}

@Command("gmc")
@Suppress("UNUSED")
fun Cm.gmc(aliases: Array<String>) {
    this("gmc", *aliases) {
        permission("essentials.gm.creative")
        optional("player", PlayerParser.playerParser())
        handler {
            gmHandler(GameMode.CREATIVE)
        }
    }
}

@Command("gma")
@Suppress("UNUSED")
fun Cm.gma(aliases: Array<String>) {
    this("gma", *aliases) {
        permission("essentials.gm.adventure")
        optional("player", PlayerParser.playerParser())
        handler {
            gmHandler(GameMode.ADVENTURE)
        }
    }
}

@Command("gmsp")
@Suppress("UNUSED")
fun Cm.gmsp(aliases: Array<String>) {
    this("gmsp", *aliases) {
        permission("essentials.gm.spectator")
        optional("player", PlayerParser.playerParser())
        handler {
            gmHandler(GameMode.SPECTATOR)
        }
    }
}

private suspend fun CommandContext<CommandSourceStack>.gmHandler(gameMode: GameMode) {
    sync {
        val sender = sender.sender
        val argPlayer = optional<Player>("player").getOrNull()
        val mode = when (gameMode) {
            GameMode.SURVIVAL -> GM_SURVIVAL
            GameMode.CREATIVE -> GM_CREATIVE
            GameMode.ADVENTURE -> GM_ADVENTURE
            GameMode.SPECTATOR -> GM_SPECTATOR
        }

        if (argPlayer != null && argPlayer.gameMode == gameMode) {
            sender.sendMessage(COMMAND_GM_FAILED_OTHER)
            return@sync
        }

        if (argPlayer != null) {
            argPlayer.gameMode = gameMode
            sender.sendMessage(
                COMMAND_GM_OTHER_SUCCCEED
                    .replace("<player>", argPlayer.name)
                    .replace("<gamemode>", mode)
            )
            return@sync
        }

        if (sender !is Player) {
            sender.sendMessage(NON_PLAYER)
            return@sync
        }

        if (sender.gameMode == gameMode) {
            sender.sendMessage(COMMAND_GM_FAILED)
            return@sync
        }

        sender.gameMode = gameMode
        sender.sendMessage(
            COMMAND_GM_SUCCCEED
                .replace("<gamemode>", mode)
        )
    }
}