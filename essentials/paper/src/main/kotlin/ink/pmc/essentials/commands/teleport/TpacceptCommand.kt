package ink.pmc.essentials.commands.teleport

import ink.pmc.essentials.COMMAND_TPACCEPT_SUCCEED
import ink.pmc.essentials.COMMAND_TPDENY_SUCCEED
import ink.pmc.essentials.TELEPORT_REQUEST_DENIED_SOUND
import ink.pmc.essentials.TELEPORT_SUCCEED_SOUND
import ink.pmc.essentials.api.teleport.TeleportRequest
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.command.ensurePlayer
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

private enum class Operation {
    ACCEPT, DENY
}

@Suppress("UNUSED")
object TpacceptCommand {
    @Command("tpaccept|tpyes <request>")
    @Permission("essentials.tpaccept")
    fun CommandSender.tpaccept(
        @Argument("request", parserName = "tp-request") request: TeleportRequest
    ) = ensurePlayer {
        handleOperation(request, Operation.ACCEPT)
    }

    @Command("tpdeny|tpno|tpdecline <request>")
    @Permission("essentials.tpdeny")
    fun CommandSender.tpdeny(
        @Argument("request", parserName = "tp-request") request: TeleportRequest
    ) = ensurePlayer {
        handleOperation(request, Operation.DENY)
    }
}

private fun CommandSender.handleOperation(request: TeleportRequest, type: Operation) {
    val choice = when (type) {
        Operation.ACCEPT -> {
            request.accept()
            playSound(TELEPORT_SUCCEED_SOUND)
            COMMAND_TPACCEPT_SUCCEED
        }

        Operation.DENY -> {
            request.deny()
            playSound(TELEPORT_REQUEST_DENIED_SOUND)
            COMMAND_TPDENY_SUCCEED
        }
    }
    sendMessage(choice.replace("<player>", request.source.name))
}