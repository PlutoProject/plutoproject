package ink.pmc.driftbottle.items

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.driftbottle.plugin
import ink.pmc.framework.utils.visual.mochaText
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.ListPersistentDataType
import org.bukkit.persistence.PersistentDataType

private val messageKey = NamespacedKey(plugin, "is_message")
private val messageContentKey = NamespacedKey(plugin, "message_content")
private val stringList = PersistentDataType.LIST.listTypeFrom(ListPersistentDataType.STRING)

internal val ItemStack.isMessage: Boolean
    get() = persistentDataContainer.get(messageKey, PersistentDataType.BOOLEAN) ?: false

internal var ItemStack.messageContent: List<String>
    get() = persistentDataContainer.get(
        messageContentKey,
        stringList
    )!!
    set(value) {
        editMeta {
            it.persistentDataContainer.set(
                messageContentKey,
                stringList,
                value
            )
        }
    }

class MessageItem : ItemStack(Material.PAPER, 1) {
    init {
        editMeta {
            it.displayName(component {
                text("漂流信纸") with mochaText without italic()
            })
            it.persistentDataContainer.apply {
                set(messageKey, PersistentDataType.BOOLEAN, true)
                set(messageContentKey, stringList, listOf())
            }
        }
    }
}