package ink.pmc.essentials.item

import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.essentials.plugin
import ink.pmc.utils.dsl.ItemStackDsl
import ink.pmc.utils.dsl.itemStack
import ink.pmc.utils.visual.mochaLavender
import ink.pmc.utils.visual.mochaSubtext0
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

val MENU_ITEM_KEY = NamespacedKey(plugin, "menu_item")

val MENU_ITEM = itemStack(Material.BOOK) {
    meta {
        setEnchantmentGlintOverride(true)
        persistentDataContainer.set(MENU_ITEM_KEY, PersistentDataType.BOOLEAN, true)
    }
    displayName {
        text("手账") with mochaLavender without italic()
    }
    lore {
        text("记录着未尽之事的书。") with mochaSubtext0 without italic()
    }
    lore {
        text("若不慎丢失的话，可以在工作台里再打造一本。") with mochaSubtext0 without italic()
    }
}

val ItemStack.isMenuItem: Boolean
    get() {
        return itemMeta.persistentDataContainer.getOrDefault(
            MENU_ITEM_KEY,
            PersistentDataType.BOOLEAN,
            false
        )
    }