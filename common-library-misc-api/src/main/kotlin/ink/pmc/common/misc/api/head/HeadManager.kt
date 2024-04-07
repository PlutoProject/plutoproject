package ink.pmc.common.misc.api.head

import org.bukkit.inventory.ItemStack
import java.util.UUID

@Suppress("UNUSED")
interface HeadManager {

    fun getHead(uuid: UUID): ItemStack?

    suspend fun getHead(name: String): ItemStack?

    fun isNameCached(name: String): Boolean

    fun isHeadCached(uuid: UUID): Boolean

}