package ink.pmc.menu.commands

import cafe.adriel.voyager.navigator.Navigator
import ink.pmc.framework.interactive.GuiManager
import ink.pmc.framework.utils.command.ensurePlayer
import ink.pmc.menu.screens.YumeMainMenuScreen
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.Command

@Suppress("UNUSED")
object MenuCommand {
    @Command("menu")
    fun CommandSender.menu() = ensurePlayer {
        GuiManager.startInventory(this) {
            Navigator(YumeMainMenuScreen())
        }
    }
}