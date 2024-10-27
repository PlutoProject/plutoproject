package ink.pmc.essentials.commands.warp

import cafe.adriel.voyager.navigator.Navigator
import ink.pmc.essentials.VIEWER_PAGING_SOUND
import ink.pmc.essentials.screens.warp.WarpViewerScreen
import ink.pmc.framework.interactive.GuiManager
import ink.pmc.framework.utils.command.ensurePlayer
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
object WarpsCommand {
    @Command("warps")
    @Permission("essentials.warps")
    fun CommandSender.warps() = ensurePlayer {
        GuiManager.startInventory(this) {
            Navigator(WarpViewerScreen())
        }
        playSound(VIEWER_PAGING_SOUND)
    }
}