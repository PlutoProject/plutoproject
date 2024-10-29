package ink.pmc.driftbottle.models

import ink.pmc.driftbottle.api.BottleOperationType
import java.time.Instant

data class BottleOperationModel(
    val time: Instant,
    val operation: BottleOperationType,
    val operator: String
)