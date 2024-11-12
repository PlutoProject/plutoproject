package ink.pmc.framework.bridge.backend.handlers.player

import ink.pmc.framework.bridge.*
import ink.pmc.framework.bridge.backend.bridgeStub
import ink.pmc.framework.bridge.backend.handlers.NotificationHandler
import ink.pmc.framework.bridge.backend.server.localServer
import ink.pmc.framework.bridge.player.createInfoWithoutLocation
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.Notification
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperation
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperation.ContentCase.*
import ink.pmc.framework.bridge.proto.playerOperationAck
import ink.pmc.framework.bridge.world.createInfo
import ink.pmc.framework.utils.currentUnixTimestamp
import ink.pmc.framework.utils.player.uuid

object PlayerOperationHandler : NotificationHandler {
    override suspend fun handle(request: Notification) {
        val msg = request.playerOperation
        val playerUuid = request.playerOperation.playerUuid.uuid
        if (msg.executor != internalBridge.local.id) return
        val localPlayer = internalBridge.getInternalLocalPlayer(playerUuid)
            ?: throwLocalPlayerNotFound(playerUuid.toString())
        debugInfo("PlayerOperationHandler: $request, $currentUnixTimestamp")
        when (msg.contentCase!!) {
            INFO_LOOKUP -> {
                val result = bridgeStub.ackPlayerOperation(playerOperationAck {
                    id = request.playerOperation.id
                    ok = true
                    infoLookup = localPlayer.createInfoWithoutLocation().toBuilder().apply {
                        location = localPlayer.location.await().createInfo()
                    }.build()
                })
                checkCommonResult(result)
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
                ) ?: throwLocalWorldNotFound(msg.teleport.world)
                localPlayer.teleport(location)
            }

            PERFORM_COMMAND -> localPlayer.performCommand(msg.performCommand)
            PlayerOperation.ContentCase.CONTENT_NOT_SET -> throwContentNotSet("PlayerOperation")
        }
        val result = bridgeStub.ackPlayerOperation(playerOperationAck {
            id = request.playerOperation.id
            ok = true
        })
        checkCommonResult(result)
    }
}