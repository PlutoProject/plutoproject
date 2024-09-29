package ink.pmc.essentials.listeners

import ink.pmc.advkt.showTitle
import ink.pmc.advkt.title.*
import ink.pmc.essentials.COMMAND_TPACCEPT_SUCCEED
import ink.pmc.essentials.COMMAND_TPDENY_SUCCEED
import ink.pmc.essentials.TELEPORT_REQUEST_DENIED_SOUND
import ink.pmc.essentials.TELEPORT_SUCCEED_SOUND
import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.utils.bedrock.isFloodgate
import ink.pmc.utils.chat.replace
import ink.pmc.utils.concurrent.submitAsync
import net.kyori.adventure.util.Ticks
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import java.util.*

@Suppress("UNUSED", "UnusedReceiverParameter")
object ActionListener : Listener, KoinComponent {

    private val config by lazy { get<EssentialsConfig>().Action() }
    private val teleportManager by inject<TeleportManager>()
    private val bePitchAntiShake = Collections.synchronizedSet<Player>(mutableSetOf())

    /*
    * Java 版菜单动作
    * Shift + F
    * */
    @EventHandler
    fun PlayerSwapHandItemsEvent.e() {
        if (!config.sneakSwapMenu) return
        if (player.isFloodgate) return
        if (!player.isSneaking) return
        isCancelled = true
        player.performCommand("menu:menu")
    }

    /*
    * 基岩版菜单 & 传送操作
    * */
    @EventHandler(ignoreCancelled = true)
    fun PlayerMoveEvent.e() {
        if (!player.isFloodgate) return
        if (!player.isSneaking) {
            bePitchAntiShake.remove(player)
            return
        }

        // 基岩版最多抬头到 -89.9994 左右，需要用区间判断
        when (to.pitch) {
            // 抬头
            in -90F..-89F -> {
                teleportManager.getPendingRequest(player)?.let {
                    if (!config.bedrockTeleportOperation) return@let
                    submitAsync { it.accept() }
                    player.showTitle {
                        subTitle(COMMAND_TPACCEPT_SUCCEED.replace("<player>", it.source.name))
                        times {
                            fadeIn(Ticks.duration(5))
                            stay(Ticks.duration(35))
                            fadeOut(Ticks.duration(20))
                        }
                    }
                    player.playSound(TELEPORT_SUCCEED_SOUND)
                    return
                }
                if (!config.bedrockMenu) return
                if (bePitchAntiShake.contains(player)) return
                bePitchAntiShake.add(player) // 菜单打开可能有延迟，提前加入防抖列表避免多次触发
                player.performCommand("menu:menu")
            }

            // 低头
            in 89F..90F -> {
                teleportManager.getPendingRequest(player)?.let {
                    if (!config.bedrockTeleportOperation) return@let
                    if (bePitchAntiShake.contains(player)) return@let
                    bePitchAntiShake.add(player)
                    submitAsync { it.deny() }
                    player.showTitle {
                        subTitle(COMMAND_TPDENY_SUCCEED.replace("<player>", it.source.name))
                        times {
                            fadeIn(Ticks.duration(5))
                            stay(Ticks.duration(35))
                            fadeOut(Ticks.duration(20))
                        }
                    }
                    player.playSound(TELEPORT_REQUEST_DENIED_SOUND)
                    return
                }
            }

            else -> {
                bePitchAntiShake.remove(player)
            }
        }

    }

}