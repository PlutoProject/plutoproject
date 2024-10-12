package ink.pmc.essentials.commands.teleport

import ink.pmc.essentials.*
import ink.pmc.essentials.api.Essentials
import ink.pmc.utils.BukkitCommandContext
import ink.pmc.utils.BukkitCommandManager
import ink.pmc.utils.annotation.Command
import ink.pmc.utils.chat.replace
import ink.pmc.utils.command.bukkitOptionalOnlinePlayersArgument
import ink.pmc.utils.command.checkPlayer
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import ink.pmc.utils.player.uuidOrNull
import org.bukkit.Bukkit
import kotlin.jvm.optionals.getOrNull

private enum class Operation {
    ACCEPT, DENY
}

@Command("tpaccept")
@Suppress("UNUSED")
fun BukkitCommandManager.tpaccept(aliases: Array<String>) {
    this("tpaccept", *aliases) {
        permission("essentials.tpaccept")
        argument(bukkitOptionalOnlinePlayersArgument("request"))
        handler {
            handleOperation(Operation.ACCEPT)
        }
    }
}

@Command("tpdeny")
@Suppress("UNUSED")
fun BukkitCommandManager.tpdeny(aliases: Array<String>) {
    this("tpdeny", *aliases) {
        permission("essentials.tpdeny")
        argument(bukkitOptionalOnlinePlayersArgument("request"))
        handler {
            handleOperation(Operation.DENY)
        }
    }
}

private suspend fun BukkitCommandContext.handleOperation(type: Operation) {
    checkPlayer(sender) {
        val manager = Essentials.teleportManager
        val input = optional<String>("request").getOrNull()

        val argPlayer = input?.let { Bukkit.getPlayer(it) }
        val argUuid = input?.uuidOrNull

        val emptyArgRequest = manager.getPendingRequest(this) // 没有参数的情况
        val playerArgRequest = argPlayer?.let { manager.getUnfinishedRequest(it) } // 参数是玩家名情况
        val uuidArgRequest = argUuid?.let { manager.getRequest(it) } // 参数是请求 ID 的情况

        if (input != null) {
            val destination = uuidArgRequest?.destination ?: playerArgRequest?.destination

            if (argUuid != null && (uuidArgRequest == null || uuidArgRequest.isFinished || destination != player)) {
                sendMessage(COMMAND_TPACCEPT_FAILED_NO_REQUEST_ID)
                playSound(TELEPORT_FAILED_SOUND)
                return@checkPlayer
            }

            if (argPlayer != null && (playerArgRequest == null || playerArgRequest.isFinished || destination != player)) {
                sendMessage(COMMAND_TPACCEPT_FAILED_NO_REQUEST.replace("<player>", argPlayer.name))
                playSound(TELEPORT_FAILED_SOUND)
                return@checkPlayer
            }

            if (argUuid == null && argPlayer == null) {
                sendMessage(COMMAND_TPACCEPT_FAILED_NO_REQUEST.replace("<player>", input)) // 两种都不就当作玩家名发送错误消息
                playSound(TELEPORT_FAILED_SOUND)
                return@checkPlayer
            }
        }

        if (emptyArgRequest == null && playerArgRequest == null && uuidArgRequest == null) {
            sendMessage(COMMAND_TPACCEPT_FAILED_NO_PENDING)
            return@checkPlayer
        }

        // 只有一个 request 存在，所以只有一个会被处理
        val choice = when (type) {
            Operation.ACCEPT -> {
                emptyArgRequest?.accept()
                playerArgRequest?.accept()
                uuidArgRequest?.accept()
                playSound(TELEPORT_SUCCEED_SOUND)
                COMMAND_TPACCEPT_SUCCEED
            }

            Operation.DENY -> {
                emptyArgRequest?.deny()
                playerArgRequest?.deny()
                uuidArgRequest?.deny()
                playSound(TELEPORT_REQUEST_DENIED_SOUND)
                COMMAND_TPDENY_SUCCEED
            }
        }

        val name = emptyArgRequest?.source?.name
            ?: playerArgRequest?.source?.name
            ?: uuidArgRequest?.source?.name
        sendMessage(choice.replace("<player>", name))
    }
}