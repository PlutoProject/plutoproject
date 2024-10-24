package ink.pmc.essentials.commands.warp

import cafe.adriel.voyager.navigator.Navigator
import ink.pmc.essentials.Cm
import ink.pmc.essentials.VIEWER_PAGING_SOUND
import ink.pmc.essentials.screens.warp.WarpViewerScreen
import ink.pmc.interactive.api.GuiManager
import ink.pmc.framework.utils.command.annotation.Command
import ink.pmc.framework.utils.command.checkPlayer
import ink.pmc.framework.utils.dsl.cloud.invoke
import ink.pmc.framework.utils.dsl.cloud.sender

@Command("warps")
@Suppress("UNUSED")
fun Cm.warps(aliases: Array<String>) {
    this("warps", *aliases) {
        permission("essentials.warps")
        handler {
            checkPlayer(sender.sender) {
                GuiManager.startInventory(this) {
                    Navigator(WarpViewerScreen())
                }
                playSound(VIEWER_PAGING_SOUND)
            }
        }
    }
}