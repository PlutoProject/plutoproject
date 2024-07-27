package ink.pmc.essentials.commands

import ink.pmc.essentials.*
import ink.pmc.essentials.api.Essentials
import ink.pmc.utils.chat.NON_PLAYER
import ink.pmc.utils.chat.NO_PERMISSON
import ink.pmc.utils.chat.replace
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import org.bukkit.entity.Player
import org.incendo.cloud.bukkit.parser.PlayerParser
import kotlin.jvm.optionals.getOrNull

@Command("tpcancel")
@Suppress("UNUSED")
fun Cm.tpcancel(aliases: Array<String>) {
    this("tpcancel", *aliases) {
        permission("essentials.tpcancel")
        optional("player", PlayerParser.playerParser())
        handler {
            val manager = Essentials.teleportManager
            val argPlayer = optional<Player>("player").getOrNull()
            val argRequest = argPlayer?.let { manager.getUnfinishedRequest(it) }
            val sender = sender.sender

            if (argPlayer != null) {
                if (!sender.hasPermission("essentials.tpcancel.other")) {
                    sender.sendMessage(NO_PERMISSON)
                    return@handler
                }

                if (argRequest == null) {
                    sender.sendMessage(
                        COMMAND_TPCANCEL_NO_REQUEST_OTHER
                            .replace("<player>", argPlayer.name)
                    )
                    return@handler
                }

                argRequest.cancel()
                sender.sendMessage(
                    COMMAND_TPCANCEL_SUCCEED_OTHER
                        .replace("<player>", argPlayer.name)
                        .replace("<dest>", argRequest.destination.name)
                )
                argPlayer.sendMessage(
                    COMMAND_TPCANCEL_OTHER_NOTIFY
                        .replace("<player>", argRequest.destination.name)
                )

                return@handler
            }

            if (sender !is Player) {
                sender.sendMessage(NON_PLAYER)
                return@handler
            }

            val request = manager.getUnfinishedRequest(sender) ?: return@handler run {
                sender.sendMessage(COMMAND_TPCANCEL_NO_REQUEST)
            }

            request.cancel()
            sender.sendMessage(
                COMMAND_TPCANCEL_SUCCEED
                    .replace("<player>", request.destination.name)
            )
        }
    }
}