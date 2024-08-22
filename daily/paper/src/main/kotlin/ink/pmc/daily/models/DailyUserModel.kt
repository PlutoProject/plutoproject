package ink.pmc.daily.models

import ink.pmc.daily.api.DailyUser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.ZoneOffset

internal fun DailyUser.toModel(): DailyUserModel {
    return DailyUserModel(
        id = id.toString(),
        lastCheckIn = lastCheckIn?.toInstant(ZoneOffset.UTC)?.toEpochMilli(),
        accumulatedDays = accumulatedDays,
    )
}

@Serializable
data class DailyUserModel(
    @SerialName("_id") val id: String,
    val lastCheckIn: Long?,
    val accumulatedDays: Int,
)