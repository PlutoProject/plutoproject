package ink.pmc.driftbottle.api

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.time.Instant
import java.util.*

interface Bottle {
    val id: UUID
    val creator: UUID
    val createdAt: Instant
    val content: List<String>
    var state: BottleState
    val operations: List<BottleOperation>
    val item: ItemStack

    fun logOperation(operator: Player, type: BottleOperationType)

    suspend fun saveOrUpdate()
}