package ink.pmc.essentials.commands

import ink.pmc.essentials.*
import ink.pmc.utils.BukkitCommandManager
import ink.pmc.utils.annotation.Command
import ink.pmc.utils.chat.NO_PERMISSON
import ink.pmc.utils.chat.replace
import ink.pmc.utils.command.checkPlayer
import ink.pmc.utils.concurrent.sync
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.incendo.cloud.bukkit.parser.PlayerParser
import kotlin.jvm.optionals.getOrNull

@Command("hat")
@Suppress("UNUSED")
fun BukkitCommandManager.hat(aliases: Array<String>) {
    this("hat", *aliases) {
        permission("essentials.hat")
        optional("player", PlayerParser.playerParser())
        handler {
            checkPlayer(sender) {
                val argPlayer = optional<Player>("player").getOrNull()

                if (handItem.type == Material.AIR) {
                    sendMessage(COMMAND_HAT_FAILED_EMPTY_HAND)
                    return@checkPlayer
                }

                if (argPlayer != null) {
                    if (!hasPermission("essentials.hat.other")) {
                        sendMessage(NO_PERMISSON)
                        return@checkPlayer
                    }

                    if (argPlayer.hatItem != null) {
                        sendMessage(COMMAND_HAT_FAILED_EXISTED_OTHER)
                        return@checkPlayer
                    }

                    argPlayer.hat(handItem)
                    clearHand()
                    sendMessage(COMMAND_HAT_SUCCEED_OTHER.replace("<player>", argPlayer.name))
                    return@checkPlayer
                }

                val keepHatItem = hatItem
                hat(handItem)
                clearHand()

                if (keepHatItem != null) {
                    hand(keepHatItem)
                }

                sendMessage(COMMAND_HAT_SUCCEED)
            }
        }
    }
}

private val Player.handItem: ItemStack
    get() = inventory.itemInMainHand

private val Player.hatItem: ItemStack?
    get() = inventory.helmet

private suspend fun Player.hand(item: ItemStack) {
    sync {
        inventory.setItemInMainHand(item)
    }
}

private suspend fun Player.hat(item: ItemStack) {
    sync {
        inventory.helmet = item
    }
}

private suspend fun Player.clearHand() {
    sync {
        hand(ItemStack(Material.AIR))
    }
}