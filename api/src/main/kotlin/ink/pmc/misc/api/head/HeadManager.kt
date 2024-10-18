package ink.pmc.misc.api.head

import ink.pmc.utils.inject.inlinedGet
import org.bukkit.inventory.ItemStack
import java.util.*

@Suppress("UNUSED")
interface HeadManager {

    companion object : HeadManager by inlinedGet()

    fun getHead(uuid: UUID): ItemStack?

    suspend fun getHead(name: String): ItemStack?

    fun isNameCached(name: String): Boolean

    fun isHeadCached(uuid: UUID): Boolean

}