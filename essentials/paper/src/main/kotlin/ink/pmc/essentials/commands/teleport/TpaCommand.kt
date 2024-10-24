package ink.pmc.essentials.commands.teleport

import ink.pmc.essentials.*
import ink.pmc.essentials.api.Essentials
import ink.pmc.essentials.api.teleport.TeleportDirection
import ink.pmc.essentials.api.teleport.TeleportDirection.COME
import ink.pmc.essentials.api.teleport.TeleportDirection.GO
import ink.pmc.framework.utils.command.annotation.Command
import ink.pmc.framework.utils.chat.DURATION
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.command.checkPlayer
import ink.pmc.framework.utils.dsl.cloud.invoke
import ink.pmc.framework.utils.dsl.cloud.sender
import org.bukkit.entity.Player
import org.incendo.cloud.bukkit.parser.PlayerParser

@Command("tpa")
@Suppress("UNUSED")
fun Cm.tpa(aliases: Array<String>) {
    this("tpa", *aliases) {
        permission("essentials.tpa")
        required("player", PlayerParser.playerParser())
        handler {
            checkPlayer(sender.sender) {
                val target = get<Player>("player")
                handleTpa(this, target, GO)
            }
        }
    }
}

@Command("tpahere")
@Suppress("UNUSED")
fun Cm.tpahere(aliases: Array<String>) {
    this("tpahere", *aliases) {
        permission("essentials.tpahere")
        required("player", PlayerParser.playerParser())
        handler {
            checkPlayer(sender.sender) {
                val target = get<Player>("player")
                handleTpa(this, target, COME)
            }
        }
    }
}

private fun handleTpa(source: Player, destination: Player, direction: TeleportDirection) {
    val manager = Essentials.teleportManager

    if (destination == source) {
        source.sendMessage(COMMAND_TPA_FAILED_SELF)
        return
    }

    if (manager.hasPendingRequest(destination)) {
        source.sendMessage(COMMAND_TPA_FAILED_TARGET_BUSY)
        return
    }

    if (direction == GO && manager.isBlacklisted(destination.world) && !source.hasPermission(BYPASS_WORLD_BLACKLIST)) {
        source.sendMessage(
            COMMAND_TPA_FAILED_NOT_ALLOWED_GO
                .replace("<player>", source.name)
        )
        source.playSound(TELEPORT_FAILED_SOUND)
        return
    }

    if (direction == COME && manager.isBlacklisted(source.world) && !source.hasPermission(BYPASS_WORLD_BLACKLIST)) {
        source.sendMessage(COMMAND_TPA_FAILED_NOT_ALLOWED_COME)
        source.playSound(TELEPORT_FAILED_SOUND)
        return
    }

    val oldRequest = manager.getUnfinishedRequest(source)

    oldRequest?.cancel()
    manager.createRequest(source, destination, direction)

    val message = when (direction) {
        GO -> COMMAND_TPA_SUCCEED
        COME -> COMMAND_TPAHERE_SUCCEED
    }

    source.sendMessage(
        message
            .replace("<player>", destination.name)
            .replace("<expire>", DURATION(manager.defaultRequestOptions.expireAfter))
    )
    if (Essentials.isAfkEnabled() && Essentials.afkManager.isAfk(destination)) {
        source.sendMessage(COMMAND_TPA_AFK)
    }
    oldRequest?.let {
        source.sendMessage(TELEPORT_REQUEST_AUTO_CANCEL.replace("<player>", oldRequest.destination.name))
    }
    source.playSound(TELEPORT_REQUEST_RECEIVED_SOUND)
}