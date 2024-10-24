package ink.pmc.essentials.dtos

import ink.pmc.framework.utils.data.UUIDSerializer
import ink.pmc.framework.utils.storage.LocationDto
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import java.util.*

@Serializable
data class BackDto(
    @SerialName("_id") @Contextual val objectId: ObjectId,
    @Serializable(UUIDSerializer::class) val owner: UUID,
    var recordedAt: Long,
    var location: LocationDto
)