package ink.pmc.serverselector

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.framework.utils.visual.mochaLavender
import ink.pmc.framework.utils.visual.mochaSubtext0
import ink.pmc.framework.utils.visual.mochaText
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

private val key = NamespacedKey(plugin, "server_selector")

val ItemStack.isServerSelector: Boolean
    get() {
        return persistentDataContainer.has(key)
    }

object ServerSelectorItem : ItemStack(Material.COMPASS) {
    init {
        editMeta {
            it.displayName(component {
                text("选择服务器") with mochaText without italic()
            })
            it.lore(buildList {
                add(component {
                    text("踏上新的旅途吧~") with mochaSubtext0 without italic()
                })
                add(Component.empty())
                add(component {
                    text("右键点击 ") with mochaLavender without italic()
                    text("选择服务器") with mochaText without italic()
                })
            })
            it.setEnchantmentGlintOverride(true)
            it.persistentDataContainer.set(key, PersistentDataType.BOOLEAN, true)
        }
    }
}