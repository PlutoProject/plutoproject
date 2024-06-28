package ink.pmc.misc.impl.head

import ink.pmc.misc.api.head.HeadManager
import ink.pmc.utils.player.getPlayerUUID
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

class HeadManagerImpl : HeadManager {

    private val cachedHeads = mutableMapOf<UUID, ItemStack>()
    private val cachedUUIDs = mutableMapOf<String, UUID>()

    override fun getHead(uuid: UUID): ItemStack? {
        if (cachedHeads.containsKey(uuid)) {
            return cachedHeads[uuid]
        }

        val head = ItemStack(Material.PLAYER_HEAD)
        val meta = head.itemMeta as SkullMeta

        val profile = Bukkit.createProfile(uuid)
        meta.playerProfile = profile
        head.itemMeta = meta
        cachedHeads[uuid] = head

        return head
    }

    override suspend fun getHead(name: String): ItemStack? {
        val lowercase = name.lowercase()
        var id: UUID

        if (!cachedUUIDs.containsKey(lowercase)) {
            val nullableID = lowercase.getPlayerUUID() ?: return null
            id = nullableID
            cachedUUIDs[lowercase] = id
        }

        id = cachedUUIDs[lowercase]!!

        return getHead(id)
    }

    override fun isNameCached(name: String): Boolean {
        return cachedUUIDs.containsKey(name)
    }

    override fun isHeadCached(uuid: UUID): Boolean {
        return cachedHeads.containsKey(uuid)
    }
}