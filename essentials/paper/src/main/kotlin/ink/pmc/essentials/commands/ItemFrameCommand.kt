package ink.pmc.essentials.commands

import ink.pmc.essentials.*
import ink.pmc.utils.chat.replace
import ink.pmc.utils.concurrent.sync
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import ink.pmc.utils.player.uuidOrNull
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.OfflinePlayer
import org.bukkit.attribute.Attribute
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

private enum class Operation {

    INVISIBLE, PROTECT

}

private val invKey = NamespacedKey("essentials", "itemframe_invsible")
private val protectKey = NamespacedKey("essentials", "itemframe_protect")
private val protectorKey = NamespacedKey("essentials", "itemframe_protector")

internal var ItemFrame.inv: Boolean
    get() = persistentDataContainer.getOrDefault(invKey, PersistentDataType.BOOLEAN, false)
    set(value) {
        persistentDataContainer.set(invKey, PersistentDataType.BOOLEAN, value)
        isVisible = !value
    }

internal val ItemFrame.protect: Boolean
    get() = persistentDataContainer.getOrDefault(protectKey, PersistentDataType.BOOLEAN, false)

internal val ItemFrame.protector: OfflinePlayer?
    get() {
        val uuid = persistentDataContainer.get(protectorKey, PersistentDataType.STRING)?.uuidOrNull ?: return null
        return Bukkit.getOfflinePlayer(uuid)
    }

internal val ItemFrame.isProtected: Boolean
    get() = protect && protector != null

internal val ItemFrame.protectorName: String
    get() = protector?.name ?: IF_PROTECT_UNKNOWN_PLAYER

private fun ItemFrame.setProtect(value: Boolean, player: Player) {
    persistentDataContainer.set(protectKey, PersistentDataType.BOOLEAN, value)
    if (value) {
        persistentDataContainer.set(protectorKey, PersistentDataType.STRING, player.uniqueId.toString())
        return
    }
    persistentDataContainer.remove(protectorKey)
}

@Command("itemframe")
@Suppress("UNUSED")
fun Cm.itemframe(aliases: Array<String>) {
    this("itemframe", *aliases) {
        "invisible" {
            permission("essentials.itemframe.invisible")
            handler {
                checkPlayer(sender.sender) {
                    sync { handleOperation(Operation.INVISIBLE) }
                }
            }
        }
        "protect" {
            permission("essentials.itemframe.protect")
            handler {
                checkPlayer(sender.sender) {
                    sync { handleOperation(Operation.PROTECT) }
                }
            }
        }
    }
}

private fun Player.handleOperation(operation: Operation) {
    val range = getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE)!!.value
    val entity = getTargetEntity(range.toInt())

    if (entity == null || entity !is ItemFrame) {
        sendMessage(COMMAND_IF_FAILED_NO_FRAME)
        return
    }

    val player = this

    fun ItemFrame.handleInvisible() {
        if (isProtected && protector != player) {
            player.sendMessage(IF_PROTECTED_ACTION.replace("<player>", protectorName))
            return
        }
        if (!inv) {
            inv = true
            player.sendMessage(COMMAND_IF_INV_ON_SUCCEED)
            return
        }
        inv = false
        player.sendMessage(COMMAND_IF_INV_OFF_SUCCEED)
    }

    fun ItemFrame.handleProtect() {
        if (isProtected && protector != player) {
            player.sendMessage(IF_PROTECTED_ACTION.replace("<player>", protectorName))
            return
        }
        if (!protect) {
            setProtect(true, player)
            player.sendMessage(COMMAND_IF_PROTECT_ON_SUCCEED)
            return
        }
        setProtect(false, player)
        player.sendMessage(COMMAND_IF_PROTECT_OFF_SUCCEED)
        return
    }

    when (operation) {
        Operation.INVISIBLE -> entity.handleInvisible()
        Operation.PROTECT -> entity.handleProtect()
    }
}