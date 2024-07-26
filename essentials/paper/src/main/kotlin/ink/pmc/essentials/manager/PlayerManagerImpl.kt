package ink.pmc.essentials.manager

import ink.pmc.essentials.api.player.PlayerManager
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.koin.core.component.KoinComponent

class PlayerManagerImpl : PlayerManager, KoinComponent {

    private val invisibleKey = NamespacedKey("essentials", "invisible")
    private val flyableKey = NamespacedKey("essentials", "flyable")

    override fun toggleInvisibility(player: Player, prompt: Boolean) {
        if (isInvisible(player)) {
            setInvisibility(player, false, prompt)
            return
        }
        setInvisibility(player, true, prompt)
    }

    override fun setInvisibility(player: Player, invisible: Boolean, prompt: Boolean) {
        TODO("Not yet implemented")
    }

    override fun isInvisible(player: Player): Boolean {
        return player.persistentDataContainer.getOrDefault(invisibleKey, PersistentDataType.BOOLEAN, false)
    }

    override fun toggleFly(player: Player, prompt: Boolean) {
        if (isFlyable(player)) {
            setFly(player, false, prompt)
            return
        }
        setFly(player, true, prompt)
    }

    override fun setFly(player: Player, flyable: Boolean, prompt: Boolean) {
        TODO("Not yet implemented")
    }

    override fun isFlyable(player: Player): Boolean {
        return player.persistentDataContainer.getOrDefault(flyableKey, PersistentDataType.BOOLEAN, false)
    }

}