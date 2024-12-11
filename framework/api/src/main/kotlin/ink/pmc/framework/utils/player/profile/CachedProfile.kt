package ink.pmc.framework.utils.player.profile

import ink.pmc.framework.utils.data.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CachedProfile(
    val rawName: String,
    val name: String,
    @Serializable(with = UUIDSerializer::class) val uuid: UUID,
)