package ink.pmc.daily.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerModel(
    @SerialName("_id") val id: String,
    val lastSign: Long,
    val accumulated: List<String>
)