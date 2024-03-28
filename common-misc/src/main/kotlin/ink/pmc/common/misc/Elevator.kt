package ink.pmc.common.misc

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import ink.pmc.common.utils.concurrent.submitAsync
import ink.pmc.common.utils.platform.isFolia
import org.bukkit.event.player.PlayerToggleSneakEvent

fun handlePlayerJumpFloorUp(event: PlayerJumpEvent) {
    val player = event.player

    /*
    * TODO: 兼容 Folia
    * 暂时没有找到在 Folia 上完美工作的办法。
    * 如果直接使用调度器，会有延迟。
    * 如果识别到 Folia 使用 runBlocking，会完全阻塞当前区域线程。
    * */
    if (isFolia) {
        return
    }

    submitAsync {
        val chain = elevatorManager.getChainAt(event.from) ?: return@submitAsync
        chain.up(player)
    }
}

fun handlePlayerSneakFloorDown(event: PlayerToggleSneakEvent) {
    if (!event.isSneaking) {
        return
    }

    if (isFolia) {
        return
    }

    val player = event.player

    submitAsync {
        val chain = elevatorManager.getChainAt(player.location) ?: return@submitAsync
        chain.down(player)
    }
}