package ink.pmc.driftbottle.api

import java.time.Instant
import java.util.*

interface BottleOperation {
    val operator: UUID
    val time: Instant
    val type: BottleOperationType
}