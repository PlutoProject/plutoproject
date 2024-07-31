package ink.pmc.essentials.commands.home

import ink.pmc.essentials.Cm
import ink.pmc.utils.chat.NON_PLAYER
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import org.bukkit.entity.Player
import org.incendo.cloud.bukkit.parser.PlayerParser
import kotlin.jvm.optionals.getOrNull

fun Cm.homes(aliases: Array<String>) {
    this("homes", *aliases) {
        permission("essentials.home")
        optional("player", PlayerParser.playerParser())
        handler {
            val sender = sender.sender
            val argPlayer = optional<Player>("player").getOrNull()

            if (argPlayer == null && sender !is Player) {
                sender.sendMessage(NON_PLAYER)
                return@handler
            }
        }
    }
}