package ink.pmc.framework.player.profile

import ink.pmc.framework.serialize.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CachedProfile(
    val rawName: String,
    val name: String,
    @Serializable(with = UUIDSerializer::class) val uuid: UUID,
)