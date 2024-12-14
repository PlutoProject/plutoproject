package ink.pmc.serverselector.storage

import ink.pmc.framework.utils.data.serializers.bson.BsonUUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UserModel(
    @Serializable(BsonUUIDSerializer::class) val uuid: UUID,
    val previouslyJoinedServer: String?
)