package ink.pmc.framework.bridge

import ink.pmc.framework.bridge.player.InternalPlayer
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerInfo

fun InternalPlayer.update(info: PlayerInfo) {
    world = server.getWorld(info.world.name) ?: error("World not found: ${info.world.name}")
}