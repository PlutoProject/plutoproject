package ink.pmc.driftbottle.models

import ink.pmc.driftbottle.api.Bottle
import ink.pmc.driftbottle.api.BottleState
import kotlinx.serialization.SerialName

fun Bottle.toModel(): BottleModel {
    return BottleModel(
        id = this.id.toString(),
        creator = this.creator.toString(),
        createdAt = this.createdAt.toEpochMilli(),
        content = this.content,
        state = this.state,
        operations = this.operations.map { it.toModel() }
    )
}

data class BottleModel(
    @SerialName("_id") val id: String,
    val creator: String,
    val createdAt: Long,
    val content: List<String>,
    val state: BottleState,
    val operations: List<BottleOperationModel>
)