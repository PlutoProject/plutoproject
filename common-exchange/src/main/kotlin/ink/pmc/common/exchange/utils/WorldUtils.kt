package ink.pmc.common.exchange.utils

import ink.pmc.common.utils.platform.paperUtilsPlugin
import org.bukkit.GameRule
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.Sign
import org.bukkit.persistence.PersistentDataType

fun disableGameRules(world: World) {
    world.setGameRule(GameRule.DO_MOB_SPAWNING, false)
    world.setGameRule(GameRule.DO_MOB_LOOT, false)
    world.setGameRule(GameRule.MOB_GRIEFING, false)
    world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
    world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
    world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
    world.time = 1000
}

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