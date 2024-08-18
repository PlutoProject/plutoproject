package ink.pmc.daily.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HistoryModel(
    @SerialName("_id")  val id: String,
    val owner: String,
    val time: Long,
)