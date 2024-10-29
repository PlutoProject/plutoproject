package ink.pmc.driftbottle.api

import org.bukkit.inventory.ItemStack
import java.time.LocalDateTime
import java.util.*

interface Bottle {
    val id: UUID
    val creator: UUID
    val createdAt: LocalDateTime
    var state: BottleState
    val operations: List<BottleOperation>
    val item: ItemStack

    fun logOperation(operator: UUID, type: BottleOperationType)

    suspend fun update()
}