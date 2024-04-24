package ink.pmc.common.member.session

import com.google.common.io.ByteStreams
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import ink.pmc.common.utils.json.toJsonString
import ink.pmc.common.utils.platform.proxy
import java.util.*

class VelocitySessionService : AbstractSessionService() {
    override fun messageAdd(type: SessionType, uuid: UUID) {

        val out = ByteStreams.newDataOutput()
        val message = SessionMessage(SessionMessageType.ADD, type, uuid)
        out.writeUTF(message.toJsonString())
        val player = proxy.getPlayer(uuid)

        player.ifPresent {
            it.sendPluginMessage(MinecraftChannelIdentifier.create(CHANNEL_NAMESPACE, CHANNEL_NAME), out.toByteArray())
        }
    }

    override fun messageRemove(type: SessionType, uuid: UUID) {
        val out = ByteStreams.newDataOutput()
        val message = SessionMessage(SessionMessageType.REMOVE, type, uuid)
        out.writeUTF(message.toJsonString())
        val player = proxy.getPlayer(uuid)

        player.ifPresent {
            it.sendPluginMessage(MinecraftChannelIdentifier.create(CHANNEL_NAMESPACE, CHANNEL_NAME), out.toByteArray())
        }
    }

    override fun init() {
        proxy.channelRegistrar.register(MinecraftChannelIdentifier.create(CHANNEL_NAMESPACE, CHANNEL_NAME))
    }

}