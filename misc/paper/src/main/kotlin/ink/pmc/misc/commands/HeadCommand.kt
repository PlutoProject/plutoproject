package ink.pmc.misc.commands

import ink.pmc.misc.*
import ink.pmc.utils.chat.NON_PLAYER
import ink.pmc.utils.chat.replace
import ink.pmc.utils.command.PaperCommand
import ink.pmc.utils.command.paperRequiredOnlinePlayersArgument
import ink.pmc.utils.visual.mochaYellow
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler

object HeadCommand : PaperCommand() {

    private val head = commandManager.commandBuilder("head")
        .argument(paperRequiredOnlinePlayersArgument())
        .suspendingHandler {
            val sender = it.sender().sender
            val name = it.get<String>("name")

            if (sender !is Player) {
                sender.sendMessage(NON_PLAYER)
                return@suspendingHandler
            }

            if (!headManager.isNameCached(name.lowercase())) {
                sender.sendMessage(HEAD_GET_LOAD_DATA)
            }

            val head = headManager.getHead(name)

            if (head == null) {
                sender.sendMessage(HEAD_GET_FAILED)
                return@suspendingHandler
            }

            val inv = sender.inventory

            if (!inv.storageContents.any { item -> item == null || item.amount == 0 }) {
                sender.sendMessage(HEAD_GET_FAILED_INV_FULL)
                return@suspendingHandler
            }

            inv.addItem(head)
            sender.sendMessage(HEAD_GET_SUCCEED.replace("<player>", Component.text(name).color(mochaYellow)))
        }

    init {
        command(head)
    }

}