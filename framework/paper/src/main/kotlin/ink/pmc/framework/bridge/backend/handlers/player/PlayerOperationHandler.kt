package ink.pmc.framework.bridge.backend.handlers.player

import ink.pmc.framework.bridge.backend.bridgeStub
import ink.pmc.framework.bridge.backend.handlers.NotificationHandler
import ink.pmc.framework.bridge.backend.operationsSent
import ink.pmc.framework.bridge.backend.server.localServer
import ink.pmc.framework.bridge.player.createInfo
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.Notification
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperation
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperation.ContentCase.*
import ink.pmc.framework.bridge.proto.playerOperationAck
import ink.pmc.framework.utils.player.uuid

object PlayerOperationHandler : NotificationHandler {
    override suspend fun handle(request: Notification) {
        val msg = request.playerOperation
        if (operationsSent.remove(msg.id.uuid)) return
        val localPlayer = localServer.getPlayer(msg.playerUuid.uuid) ?: error("Player not found: ${msg.playerUuid}")
        when (msg.contentCase!!) {
            INFO_LOOKUP -> {
                bridgeStub.ackPlayerOperation(playerOperationAck {
                    ok = true
                    infoLookup = localPlayer.createInfo()
                })
                return
            }

            SEND_MESSAGE -> error("Unexpected")
            SHOW_TITLE -> error("Unexpected")
            PLAY_SOUND -> error("Unexpected")
            TELEPORT -> {
                val location = localServer.getWorld(msg.teleport.world)?.getLocation(
                    msg.teleport.x,
                    msg.teleport.y,
                    msg.teleport.z,
                    msg.teleport.yaw,
                    msg.teleport.pitch,
                ) ?: error("World not found: ${msg.teleport.world} (server: ${msg.teleport.server})")
                localPlayer.teleport(location)
            }

            PERFORM_COMMAND -> localPlayer.performCommand(msg.performCommand)
            PlayerOperation.ContentCase.CONTENT_NOT_SET -> error("Received a PlayerOperation without content")
        }
        bridgeStub.ackPlayerOperation(playerOperationAck { ok = true })
    }
}