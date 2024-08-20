package ink.pmc.daily.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class DailyHistoryModel(
    @SerialName("_id") val id: String = UUID.randomUUID().toString(),
    val owner: String,
    val createdAt: Long,
)