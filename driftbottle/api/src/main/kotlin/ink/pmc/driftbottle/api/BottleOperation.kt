package ink.pmc.driftbottle.api

import java.time.Instant
import java.util.*

interface BottleOperation {
    val time: Instant
    val operator: UUID
    val type: BottleOperationType
}