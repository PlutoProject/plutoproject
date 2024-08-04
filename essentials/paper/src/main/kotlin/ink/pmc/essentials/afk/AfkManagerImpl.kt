package ink.pmc.essentials.afk

import ink.pmc.essentials.AFK_END_ANNOUNCE
import ink.pmc.essentials.AFK_START_ANNOUNCE
import ink.pmc.essentials.api.afk.AfkManager
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.essentials.disabled
import ink.pmc.essentials.essentialsScope
import ink.pmc.utils.chat.replace
import ink.pmc.utils.concurrent.submitAsync
import kotlinx.coroutines.delay
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toKotlinDuration

class AfkManagerImpl : AfkManager, KoinComponent {

    private val conf by lazy { get<EssentialsConfig>().Afk() }

    override val afkList: MutableList<Player> = Collections.synchronizedList(mutableListOf())
    override val idleDuration: Duration = conf.idleDuration

    init {
        essentialsScope.submitAsync {
            while (!disabled) {
                Bukkit.getOnlinePlayers().forEach {
                    val idle = it.idleDuration.toKotlinDuration()
                    if (isAfk(it)) {
                        if (idle <= idleDuration) set(it, false)
                        return@forEach
                    }
                    if (idle >= idleDuration) set(it, true)
                }
                delay(1.seconds)
            }
        }
    }

    override fun isAfk(player: Player): Boolean {
        return afkList.contains(player)
    }

    override fun set(player: Player, state: Boolean) {
        if (state && !isAfk(player)) {
            afkList.add(player)
            Bukkit.broadcast(AFK_START_ANNOUNCE.replace("<player>", player.name))
            return
        }

        if (!state && isAfk(player)) {
            afkList.remove(player)
            Bukkit.broadcast(AFK_END_ANNOUNCE.replace("<player>", player.name))
            return
        }
    }

    override fun toggle(player: Player) {
        set(player, !isAfk(player))
    }

}