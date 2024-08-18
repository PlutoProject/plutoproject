package ink.pmc.essentials.dtos

import ink.pmc.utils.data.UUIDSerializer
import ink.pmc.utils.storage.entity.LocationDto
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import java.util.*

@Serializable
data class WarpDto(
    @SerialName("_id") @Contextual val objectId: ObjectId,
    val id: @Serializable(UUIDSerializer::class) UUID,
    val name: String,
    val alias: String?,
    val createdAt: Long,
    val location: LocationDto,
    val isSpawn: Boolean,
    val isDefaultSpawn: Boolean
)