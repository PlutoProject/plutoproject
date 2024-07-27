package ink.pmc.essentials.commands

import ink.pmc.essentials.*
import ink.pmc.essentials.api.Essentials
import ink.pmc.utils.chat.replace
import ink.pmc.utils.command.paperOptionalOnlinePlayersArgument
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import ink.pmc.utils.player.uuidOrNull
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.Bukkit
import org.incendo.cloud.context.CommandContext
import kotlin.jvm.optionals.getOrNull

private enum class Operation {
    ACCEPT, DENY
}

@Command("tpaccept")
@Suppress("UNUSED")
fun Cm.tpaccept(aliases: Array<String>) {
    this("tpaccept", *aliases) {
        permission("essentials.tpaccept")
        argument(paperOptionalOnlinePlayersArgument("request"))
        handler {
            handleOperation(Operation.ACCEPT)
        }
    }
}

@Command("tpdeny")
@Suppress("UNUSED")
fun Cm.tpdeny(aliases: Array<String>) {
    this("tpdeny", *aliases) {
        permission("essentials.tpdeny")
        argument(paperOptionalOnlinePlayersArgument("request"))
        handler {
            handleOperation(Operation.DENY)
        }
    }
}

private suspend fun CommandContext<CommandSourceStack>.handleOperation(type: Operation) {
    checkPlayer(sender.sender) {
        val player = this
        val manager = Essentials.teleportManager
        val input = optional<String>("request").getOrNull()

        val argPlayer = input?.let { Bukkit.getPlayer(it) }
        val argUuid = input?.uuidOrNull

        val emptyArgRequest = manager.getPendingRequest(player) // 没有参数的情况
        val playerArgRequest = argPlayer?.let { manager.getUnfinishedRequest(it) } // 参数是玩家名情况
        val uuidArgRequest = argUuid?.let { manager.getRequest(it) } // 参数是请求 ID 的情况

        if (input != null) {
            val destination = uuidArgRequest?.destination ?: playerArgRequest?.destination

            if (argUuid != null && (uuidArgRequest == null || uuidArgRequest.isFinished || destination != player)) {
                sendMessage(COMMAND_TPACCEPT_FAILED_NO_REQUEST_ID)
                return@checkPlayer
            }

            if (argPlayer != null && (playerArgRequest == null || playerArgRequest.isFinished || destination != player)) {
                sendMessage(COMMAND_TPACCEPT_FAILED_NO_REQUEST.replace("<player>", argPlayer.name))
                return@checkPlayer
            }

            if (argUuid == null && argPlayer == null) {
                sendMessage(COMMAND_TPACCEPT_FAILED_NO_REQUEST.replace("<player>", input)) // 两种都不就当作玩家名发送错误消息
                return@checkPlayer
            }

            // 只有一个 request 存在，所以只有一个会被处理
            val choice = when (type) {
                Operation.ACCEPT -> {
                    uuidArgRequest?.accept()
                    playerArgRequest?.accept()
                    COMMAND_TPACCEPT_SUCCEED
                }

                Operation.DENY -> {
                    uuidArgRequest?.deny()
                    playerArgRequest?.deny()
                    COMMAND_TPDENY_SUCCEED
                }
            }

            val name = uuidArgRequest?.source?.name ?: playerArgRequest!!.source.name
            sendMessage(choice.replace("<player>", name))
            return@checkPlayer
        }

        if (emptyArgRequest == null) {
            sendMessage(COMMAND_TPACCEPT_FAILED_NO_PENDING)
            return@checkPlayer
        }

        emptyArgRequest.accept()
        sendMessage(COMMAND_TPACCEPT_SUCCEED.replace("<player>", emptyArgRequest.source.name))
    }
}