package ink.pmc.essentials.commands.home

import ink.pmc.essentials.Cm
import ink.pmc.essentials.TELEPORT_FAILED_SOUND
import ink.pmc.utils.annotation.Command
import ink.pmc.utils.chat.IN_PROGRESS
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import org.incendo.cloud.bukkit.parser.PlayerParser

@Command("homes")
@Suppress("UNUSED")
fun Cm.homes(aliases: Array<String>) {
    this("homes", *aliases) {
        permission("essentials.homes")
        optional("player", PlayerParser.playerParser())
        handler {
            val sender = sender.sender
            sender.sendMessage(IN_PROGRESS)
            sender.playSound(TELEPORT_FAILED_SOUND)
            return@handler

            // TODO: 家 UI
        }
    }
}