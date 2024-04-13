package ink.pmc.common.misc

import ink.pmc.common.utils.chat.NON_PLAYER
import ink.pmc.common.utils.chat.replace
import ink.pmc.common.utils.concurrent.submitAsync
import ink.pmc.common.utils.concurrent.submitSync
import ink.pmc.common.utils.visual.mochaYellow
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.incendo.cloud.parser.standard.StringParser

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
    }!!

val sitCommand = commandManager.commandBuilder("sit")
    .handler {
        val sender = it.sender()

        if (sender !is Player) {
            sender.sendMessage(NON_PLAYER)
            return@handler
        }

        sender.submitSync {
            sitManager.sit(sender, sender.location)
        }
    }!!

val headCommand = commandManager.commandBuilder("head")
    .required("name", StringParser.stringParser())
    .handler {
        submitAsync {
            val sender = it.sender()
            val name = it.get<String>("name")

            if (sender !is Player) {
                sender.sendMessage(NON_PLAYER)
                return@submitAsync
            }

            if (!headManager.isNameCached(name.lowercase())) {
                sender.sendMessage(HEAD_GET_LOAD_DATA)
            }

            val head = headManager.getHead(name)

            if (head == null) {
                sender.sendMessage(HEAD_GET_FAILED)
                return@submitAsync
            }

            val inv = sender.inventory

            if (!inv.storageContents.any { it == null || it.amount == 0 }) {
                sender.sendMessage(HEAD_GET_FAILED_INV_FULL)
                return@submitAsync
            }

            inv.addItem(head)
            sender.sendMessage(HEAD_GET_SUCCEED.replace("<player>", Component.text(name).color(mochaYellow)))
        }
    }!!