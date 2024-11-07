package ink.pmc.essentials.commands.warp

import cafe.adriel.voyager.navigator.Navigator
import ink.pmc.essentials.COMMAND_WARP_SUCCEED
import ink.pmc.essentials.COMMAND_WARP_SUCCEED_ALIAS
import ink.pmc.essentials.VIEWER_PAGING_SOUND
import ink.pmc.essentials.api.warp.Warp
import ink.pmc.essentials.screens.warp.WarpMenu
import ink.pmc.framework.interactive.GuiManager
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.command.ensurePlayer
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
object WarpCommand {
    @Command("warp [warp]")
    @Permission("essentials.warp")
    suspend fun CommandSender.warp(@Argument("warp", parserName = "warp") warp: Warp?) = ensurePlayer {
        if (warp == null) {
            GuiManager.startInventory(this) {
                Navigator(WarpMenu())
            }
            playSound(VIEWER_PAGING_SOUND)
            return
        }
        warp.teleportSuspend(this)
        if (warp.alias == null) {
            sendMessage(COMMAND_WARP_SUCCEED.replace("<name>", warp.name))
        } else {
            sendMessage(
                COMMAND_WARP_SUCCEED_ALIAS
                    .replace("<name>", warp.name)
                    .replace("<alias>", warp.alias!!)
            )
        }
    }
}