package ink.pmc.misc.commands

import ink.pmc.misc.*
import ink.pmc.misc.api.head.HeadManager
import ink.pmc.framework.utils.chat.NON_PLAYER
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.command.PaperCommand
import ink.pmc.framework.utils.command.paperRequiredOnlinePlayersArgument
import ink.pmc.framework.utils.visual.mochaYellow
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

            if (!HeadManager.isNameCached(name.lowercase())) {
                sender.sendMessage(HEAD_GET_LOAD_DATA)
            }

            val head = HeadManager.getHead(name)

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
            sender.sendMessage(HEAD_GET_SUCCEED.replace("<player>", Component.text(name)))
        }

    init {
        command(head)
    }

}