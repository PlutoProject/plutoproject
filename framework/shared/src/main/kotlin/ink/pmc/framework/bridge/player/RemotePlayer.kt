package ink.pmc.framework.bridge.player

import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperation
import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.PlayerOperationResult
import ink.pmc.framework.bridge.proto.playerOperation
import ink.pmc.framework.bridge.proto.soundInfo
import ink.pmc.framework.bridge.proto.titleInfo
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import java.util.*

abstract class RemotePlayer : InternalPlayer() {
    override suspend fun sendMessage(message: Component) {
        operatePlayer(playerOperation {
            id = UUID.randomUUID().toString()
            executor = server.id
            playerUuid = uniqueId.toString()
            sendMessage = MiniMessage.miniMessage().serialize(message)
        })
    }

    override suspend fun showTitle(title: Title) {
        operatePlayer(playerOperation {
            id = UUID.randomUUID().toString()
            executor = server.id
            playerUuid = uniqueId.toString()
            showTitle = titleInfo {
                val times = title.times()
                fadeInMs = times?.fadeIn()?.toMillis() ?: 500
                stayMs = times?.stay()?.toMillis() ?: 3500
                fadeOutMs = times?.fadeOut()?.toMillis() ?: 1000
                mainTitle = MiniMessage.miniMessage().serialize(title.title())
                subTitle = MiniMessage.miniMessage().serialize(title.subtitle())
            }
        })
    }

    override suspend fun playSound(sound: Sound) {
        operatePlayer(playerOperation {
            id = UUID.randomUUID().toString()
            executor = server.id
            playerUuid = uniqueId.toString()
            playSound = soundInfo {
                key = sound.name().asMinimalString()
                source = sound.source().toString()
                volume = sound.volume()
                pitch = sound.pitch()
            }
        })
    }

    abstract suspend fun operatePlayer(request: PlayerOperation): PlayerOperationResult
}