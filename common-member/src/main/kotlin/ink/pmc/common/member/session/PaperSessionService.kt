package ink.pmc.common.member.session

import com.google.common.io.ByteStreams
import ink.pmc.common.utils.json.toObject
import ink.pmc.common.utils.platform.paper
import ink.pmc.common.utils.platform.paperUtilsPlugin
import org.bukkit.plugin.messaging.PluginMessageListener
import java.util.*

class PaperSessionService : AbstractSessionService() {

    private val messageListener = PluginMessageListener { channel, player, bytes ->
        if (channel != FULL_CHANNEL_NAME) {
            return@PluginMessageListener
        }

        val byteInput = ByteStreams.newDataInput(bytes)
        val data = byteInput.readUTF()
        val message = data.toObject<SessionMessage>()

        if (message.uuid != player.uniqueId) {
            return@PluginMessageListener
        }

        when (message.type) {
            SessionMessageType.ADD -> {
                if (message.sessionType == SessionType.BEDROCK) {
                    bedrockSessions.add(message.uuid)
                    return@PluginMessageListener
                }

                if (message.sessionType == SessionType.LITTLESKIN) {
                    littleSkinSession.add(message.uuid)
                    return@PluginMessageListener
                }
            }

            SessionMessageType.REMOVE -> {
                if (message.sessionType == SessionType.BEDROCK) {
                    bedrockSessions.remove(message.uuid)
                    return@PluginMessageListener
                }

                if (message.sessionType == SessionType.LITTLESKIN) {
                    littleSkinSession.remove(message.uuid)
                    return@PluginMessageListener
                }
            }
        }
    }

    override fun messageAdd(type: SessionType, uuid: UUID) {
        throw UnsupportedOperationException("Unsupported on Paper.")
    }

    override fun messageRemove(type: SessionType, uuid: UUID) {
        throw UnsupportedOperationException("Unsupported on Paper.")
    }

    override fun init() {
        paper.messenger.registerIncomingPluginChannel(paperUtilsPlugin, FULL_CHANNEL_NAME, messageListener)
    }

}