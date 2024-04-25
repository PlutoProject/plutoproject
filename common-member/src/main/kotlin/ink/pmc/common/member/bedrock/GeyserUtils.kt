package ink.pmc.common.member.bedrock

import ink.pmc.common.utils.bedrock.floodgateApi
import java.util.*

val playerLinkClass: Class<*> = Class.forName("org.geysermc.floodgate.api.link.PlayerLink")
val simpleFloodgateApiClass: Class<*> = Class.forName("org.geysermc.floodgate.api.SimpleFloodgateApi")
private val linkedPlayerClass = Class.forName("org.geysermc.floodgate.util.LinkedPlayer")
private val floodgatePlayerClass = Class.forName("org.geysermc.floodgate.api.player.FloodgatePlayer")
private val instanceHolderApiClass = Class.forName("org.geysermc.floodgate.api.InstanceHolder")
private val linkedPlayerOfMethod =
    linkedPlayerClass.getDeclaredMethod("of", String::class.java, UUID::class.java, UUID::class.java)
private val correctUniqueIdMethod = floodgatePlayerClass.getDeclaredMethod("getCorrectUniqueId")
private val playerLinkField = instanceHolderApiClass.getDeclaredField("playerLink").apply { isAccessible = true }
private val floodgatePlayersField = simpleFloodgateApiClass.getDeclaredField("players").apply { isAccessible = true }

fun replacePlayerLink(playerLink: Any) {
    playerLinkField.set(null, playerLink)
}

fun newLinkedPlayer(javaUsername: String, javaUniqueId: UUID, bedrockId: UUID): Any {
    return linkedPlayerOfMethod.invoke(null, javaUsername, javaUniqueId, bedrockId)
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