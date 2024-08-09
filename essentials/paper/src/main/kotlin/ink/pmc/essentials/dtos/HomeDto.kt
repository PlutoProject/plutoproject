package ink.pmc.essentials.dtos

import ink.pmc.utils.data.UUIDSerializer
import ink.pmc.utils.storage.entity.LocationDto
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import java.util.*

@Serializable
data class HomeDto(
    @SerialName("_id") @Contextual val objectId: ObjectId,
    @Serializable(UUIDSerializer::class) val id: UUID,
    val name: String,
    val createdAt: Long,
    val location: LocationDto,
    @Serializable(UUIDSerializer::class) val owner: UUID,
    val isStarred: Boolean = false,
    val isPreferred: Boolean = false,
)