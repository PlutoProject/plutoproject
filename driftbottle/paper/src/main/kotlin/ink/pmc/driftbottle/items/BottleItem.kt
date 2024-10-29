package ink.pmc.driftbottle.items

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.driftbottle.api.Bottle
import ink.pmc.driftbottle.api.BottleManager
import ink.pmc.driftbottle.plugin
import ink.pmc.framework.utils.platform.paper
import ink.pmc.framework.utils.player.uuidOrNull
import ink.pmc.framework.utils.visual.mochaFlamingo
import ink.pmc.framework.utils.visual.mochaText
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.koin.core.component.KoinComponent
import java.util.*

private val bottleKey = NamespacedKey(plugin, "is_bottle")
private val bottleIdKey = NamespacedKey(plugin, "bottle_id")

internal val ItemStack.isBottle: Boolean
    get() = persistentDataContainer.get(bottleKey, PersistentDataType.BOOLEAN) ?: false

internal val ItemStack.bottleId: UUID?
    get() = persistentDataContainer.get(bottleIdKey, PersistentDataType.STRING)?.uuidOrNull

internal suspend fun ItemStack.getBottle(): Bottle? {
    check(isBottle) { "Not a bottle item" }
    return if (this is BottleItem) this.bottle else BottleManager.get(checkNotNull(bottleId) { "Bottle ID not present" })
}

class BottleItem(val bottle: Bottle) : ItemStack(Material.GLASS_BOTTLE, 1), KoinComponent {
    init {
        editMeta {
            it.setEnchantmentGlintOverride(true)
            it.displayName(component {
                val player = paper.getOfflinePlayer(bottle.creator)
                text("$player ") with mochaFlamingo without italic()
                text("的漂流瓶") with mochaText without italic()
            })
            it.persistentDataContainer.apply {
                set(bottleKey, PersistentDataType.BOOLEAN, true)
                set(bottleIdKey, PersistentDataType.STRING, bottle.id.toString())
            }
        }
    }
}