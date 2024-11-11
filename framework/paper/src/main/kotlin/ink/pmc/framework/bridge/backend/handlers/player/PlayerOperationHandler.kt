package ink.pmc.framework.bridge.backend.handlers.player

import ink.pmc.framework.bridge.backend.bridgeStub
import ink.pmc.framework.bridge.backend.handlers.NotificationHandler
import ink.pmc.framework.bridge.backend.operationsSent
import ink.pmc.framework.bridge.backend.server.localServer
import ink.pmc.framework.bridge.debugInfo
import ink.pmc.framework.bridge.internalBridge
import ink.pmc.framework.bridge.localPlayerNotFound
import ink.pmc.framework.bridge.localWorldNotFound
import ink.pmc.framework.bridge.player.createInfoWithoutLocation
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.Notification
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperation
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperation.ContentCase.*
import ink.pmc.framework.bridge.proto.playerOperationAck
import ink.pmc.framework.bridge.world.createInfo
import ink.pmc.framework.utils.platform.paper
import ink.pmc.framework.utils.player.uuid

object PlayerOperationHandler : NotificationHandler {
    override suspend fun handle(request: Notification) {
        val msg = request.playerOperation
        val playerUuid = request.playerOperation.playerUuid.uuid
        if (paper.getPlayer(playerUuid) == null) return
        if (operationsSent.remove(msg.id.uuid)) return
        debugInfo("PlayerOperationHandler: $request")
        val localPlayer = internalBridge.getInternalLocalPlayer(playerUuid)
            ?: localPlayerNotFound(playerUuid.toString())
        when (msg.contentCase!!) {
            INFO_LOOKUP -> {
                bridgeStub.ackPlayerOperation(playerOperationAck {
                    uuid = request.playerOperation.id
                    ok = true
                    infoLookup = localPlayer.createInfoWithoutLocation().toBuilder().apply {
                        location = localPlayer.location.await().createInfo()
                    }.build()
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
                ) ?: localWorldNotFound(msg.teleport.world)
                localPlayer.teleport(location)
            }

            PERFORM_COMMAND -> localPlayer.performCommand(msg.performCommand)
            PlayerOperation.ContentCase.CONTENT_NOT_SET -> error("Received a PlayerOperation without content")
        }
        bridgeStub.ackPlayerOperation(playerOperationAck {
            uuid = request.playerOperation.id
            ok = true
        })
    }
}