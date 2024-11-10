package ink.pmc.framework.bridge.backend.handlers

import ink.pmc.framework.bridge.backend.handlers.player.*
import ink.pmc.framework.bridge.backend.handlers.server.ServerInfoUpdateHandler
import ink.pmc.framework.bridge.backend.handlers.server.ServerOfflineHandler
import ink.pmc.framework.bridge.backend.handlers.server.ServerOnlineHandler
import ink.pmc.framework.bridge.backend.handlers.server.ServerRegistrationHandler
import ink.pmc.framework.bridge.backend.handlers.world.WorldInfoUpdateHandler
import ink.pmc.framework.bridge.backend.handlers.world.WorldLoadHandler
import ink.pmc.framework.bridge.backend.handlers.world.WorldOperationHandler
import ink.pmc.framework.bridge.backend.handlers.world.WorldUnloadHandler
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.Notification
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.Notification.ContentCase
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.Notification.ContentCase.*

interface NotificationHandler {
    companion object {
        private val handlers = mapOf(
            SERVER_REGISTRATION to ServerRegistrationHandler,
            SERVER_INFO_UPDATE to ServerInfoUpdateHandler,
            SERVER_OFFLINE to ServerOfflineHandler,
            SERVER_ONLINE to ServerOnlineHandler,

            PLAYER_OPERATION to PlayerOperationHandler,
            PLAYER_INFO_UPDATE to PlayerInfoUpdateHandler,
            PLAYER_SWITCH_SERVER to PlayerSwitchServerHandler,
            PLAYER_JOIN to PlayerJoinHandler,
            PLAYER_DISCONNECT to PlayerDisconnectHandler,

            WORLD_OPERATION to WorldOperationHandler,
            WORLD_INFO_UPDATE to WorldInfoUpdateHandler,
            WORLD_LOAD to WorldLoadHandler,
            WORLD_UNLOAD to WorldUnloadHandler,
        )

        operator fun get(type: ContentCase): NotificationHandler {
            return handlers[type] ?: error("Handler for $type not found")
        }
    }

    suspend fun handle(request: Notification)
}