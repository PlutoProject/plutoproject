package ink.pmc.essentials.items

import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.essentials.plugin
import ink.pmc.framework.utils.dsl.itemStack
import ink.pmc.framework.utils.visual.mochaLavender
import ink.pmc.framework.utils.visual.mochaSubtext0
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

private val key = NamespacedKey(plugin, "server_selector_item")

val SERVER_SELECTOR_ITEM = itemStack(Material.COMPASS) {
    meta {
        setEnchantmentGlintOverride(true)
        persistentDataContainer.set(key, PersistentDataType.BOOLEAN, true)
    }
    displayName {
        text("选择服务器") with mochaLavender without italic()
    }
    lore {
        text("下一个目标是？") with mochaSubtext0 without italic()
    }
}

val ItemStack.isServerSelectorItem: Boolean
    get() {
        return itemMeta?.persistentDataContainer?.getOrDefault(
            key,
            PersistentDataType.BOOLEAN,
            false
        ) ?: false
    }