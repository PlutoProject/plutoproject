package ink.pmc.daily.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DailyUserModel(
    @SerialName("_id") val id: String,
    val lastCheckIn: Long,
    val accumulatedDays: Int,
)