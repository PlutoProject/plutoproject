package ink.pmc.essentials.models

import ink.pmc.framework.utils.data.UUIDSerializer
import ink.pmc.framework.utils.storage.LocationModel
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import java.util.*

@Serializable
data class HomeModel(
    @SerialName("_id") @Contextual val objectId: ObjectId,
    @Serializable(UUIDSerializer::class) val id: UUID,
    val name: String,
    val createdAt: Long,
    val location: LocationModel,
    @Serializable(UUIDSerializer::class) val owner: UUID,
    val isStarred: Boolean = false,
    val isPreferred: Boolean = false,
)