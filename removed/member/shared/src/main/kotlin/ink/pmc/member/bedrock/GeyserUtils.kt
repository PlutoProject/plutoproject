package ink.pmc.member.bedrock

import ink.pmc.framework.utils.bedrock.floodgateApi
import org.geysermc.floodgate.api.link.PlayerLink
import org.geysermc.floodgate.util.LinkedPlayer
import java.util.*

val simpleFloodgateApiClass: Class<*> = Class.forName("org.geysermc.floodgate.api.SimpleFloodgateApi")
private val floodgatePlayerClass = Class.forName("org.geysermc.floodgate.api.player.FloodgatePlayer")
private val instanceHolderApiClass = Class.forName("org.geysermc.floodgate.api.InstanceHolder")
private val correctUniqueIdMethod = floodgatePlayerClass.getDeclaredMethod("getCorrectUniqueId")
private val playerLinkField = instanceHolderApiClass.getDeclaredField("playerLink").apply { isAccessible = true }
private val floodgatePlayersField = simpleFloodgateApiClass.getDeclaredField("players").apply { isAccessible = true }

fun replacePlayerLinkInstance(playerLink: PlayerLink) {
    playerLinkField.set(null, playerLink)
}

fun newLinkedPlayerInstance(javaUsername: String, javaUniqueId: UUID, bedrockId: UUID): LinkedPlayer {
    return LinkedPlayer.of(javaUsername, javaUniqueId, bedrockId)
}

@Suppress("UNCHECKED_CAST")
fun addFloodgatePlayer(floodgatePlayer: Any): Any {
    val map = floodgatePlayersField.get(floodgateApi) as MutableMap<UUID, Any>
    val uuid = correctUniqueIdMethod.invoke(floodgatePlayer) as UUID
    map[uuid] = floodgatePlayer
    return floodgatePlayer
}

@Suppress("UNCHECKED_CAST")
fun removeFloodgatePlayer(uuid: UUID) {
    val map = floodgatePlayersField.get(floodgateApi) as MutableMap<UUID, Any>
    map.remove(uuid)
}