package ink.pmc.exchange.utils

import ink.pmc.utils.platform.paperUtilsPlugin
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.Sign
import org.bukkit.persistence.PersistentDataType

private val checkoutSignKey = NamespacedKey(paperUtilsPlugin, "checkout_sign")

fun markAsCheckoutSign(block: Block) {
    val state = block.state

    if (state !is Sign) {
        return
    }

    state.persistentDataContainer.set(checkoutSignKey, PersistentDataType.BOOLEAN, true)
    state.update()
}

fun isCheckoutSign(block: Block): Boolean {
    val state = block.state

    if (state !is Sign) {
        return false
    }

    return state.persistentDataContainer.get(checkoutSignKey, PersistentDataType.BOOLEAN) ?: false
}