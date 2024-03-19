package ink.pmc.common.misc

import ink.pmc.common.utils.chat.NON_PLAYER
import ink.pmc.common.utils.concurrent.submitSync
import org.bukkit.entity.Player

val suicideCommand = commandManager.commandBuilder("suicide")
    .handler {
        val sender = it.sender()

        if (sender !is Player) {
            sender.sendMessage(NON_PLAYER)
            return@handler
        }

        sender.submitSync {
            sender.health = 0.0
            sender.sendMessage(SUICIDE)
        }
    }

val sitCommand = commandManager.commandBuilder("sit")
    .handler {
        val sender = it.sender()

        if (sender !is Player) {
            sender.sendMessage(NON_PLAYER)
            return@handler
        }

        sender.submitSync {
            sitManager.sit(sender, sender.location.subtract(0.0, 1.0, 0.0))
        }
    }