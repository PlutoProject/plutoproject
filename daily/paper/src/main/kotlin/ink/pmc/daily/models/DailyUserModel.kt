package ink.pmc.daily.models

import ink.pmc.daily.api.DailyUser
import ink.pmc.framework.utils.time.currentZoneId
import ink.pmc.framework.utils.time.toOffset
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal fun DailyUser.toModel(): DailyUserModel {
    return DailyUserModel(
        id = id.toString(),
        lastCheckIn = lastCheckIn?.toInstant(currentZoneId.toOffset())?.toEpochMilli(),
        accumulatedDays = accumulatedDays,
    )
}

@Serializable
data class DailyUserModel(
    @SerialName("_id") val id: String,
    val lastCheckIn: Long?,
    val accumulatedDays: Int,
)