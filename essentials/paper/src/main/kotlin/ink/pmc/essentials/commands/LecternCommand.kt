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
import org.bukkit.block.Lectern
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

private val protectKey = NamespacedKey("essentials", "lectern_protect")
private val protectorKey = NamespacedKey("essentials", "lectern_protector")

internal val Lectern.protect: Boolean
    get() = persistentDataContainer.getOrDefault(protectKey, PersistentDataType.BOOLEAN, false)

internal val Lectern.protector: OfflinePlayer?
    get() {
        val uuid = persistentDataContainer.get(protectorKey, PersistentDataType.STRING)?.uuidOrNull ?: return null
        return Bukkit.getOfflinePlayer(uuid)
    }

internal val Lectern.isProtected: Boolean
    get() = protect && protector != null

internal val Lectern.protectorName: String
    get() = protector?.name ?: IF_PROTECT_UNKNOWN_PLAYER

private fun Lectern.setProtect(value: Boolean, player: Player) {
    persistentDataContainer.set(protectKey, PersistentDataType.BOOLEAN, value)
    if (value) {
        persistentDataContainer.set(protectorKey, PersistentDataType.STRING, player.uniqueId.toString())
        update()
        return
    }
    persistentDataContainer.remove(protectorKey)
    update()
}

@Command("lectern")
@Suppress("UNUSED")
fun Cm.lectern(aliases: Array<String>) {
    this("lectern", *aliases) {
        permission("essentials.lectern")
        handler {
            checkPlayer(sender.sender) {
                sync {
                    val range = getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE)!!.value
                    val block = getTargetBlockExact(range.toInt())?.state
                    val player = this@checkPlayer

                    if (block == null || block !is Lectern) {
                        sendMessage(COMMAND_LECT_FAILED_NO_LECTERN)
                        return@sync
                    }

                    if (block.isProtected && block.protector != player && !player.hasPermission(LECTERN_PROTECT_BYPASS)) {
                        sendMessage(LECT_PROTECTED_ACTION.replace("<player>", block.protectorName))
                        return@sync
                    }

                    if (!block.protect) {
                        block.setProtect(true, player)
                        sendMessage(COMMAND_LECT_PROTECT_ON_SUCCEED)
                        return@sync
                    }

                    block.setProtect(false, player)
                    sendMessage(COMMAND_LECT_PROTECT_OFF_SUCCEED)
                    return@sync
                }
            }
        }
    }
}