package ink.pmc.driftbottle.models

import ink.pmc.driftbottle.api.BottleState
import kotlinx.serialization.SerialName

data class BottleModel(
    @SerialName("_id") val id: String,
    val creator: String,
    val createdAt: Long,
    val state: BottleState,
    val operations: List<BottleOperationModel>
)