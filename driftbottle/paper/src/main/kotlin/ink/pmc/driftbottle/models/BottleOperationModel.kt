package ink.pmc.driftbottle.models

import ink.pmc.driftbottle.api.BottleOperation
import ink.pmc.driftbottle.api.BottleOperationType

fun BottleOperation.toModel(): BottleOperationModel {
    return BottleOperationModel(
        time = this.time.toEpochMilli(),
        operator = this.operator.toString(),
        type = this.type
    )
}

data class BottleOperationModel(
    val time: Long,
    val operator: String,
    val type: BottleOperationType,
)