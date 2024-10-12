package ink.pmc.menu.commands

import cafe.adriel.voyager.navigator.Navigator
import ink.pmc.interactive.api.GuiManager
import ink.pmc.menu.screens.YumeMainMenuScreen
import ink.pmc.utils.BukkitCommandManager
import ink.pmc.utils.annotation.Command
import ink.pmc.utils.command.checkPlayer
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender

@Command("menu")
@Suppress("UNUSED")
fun BukkitCommandManager.menu(aliases: Array<String>) {
    this("menu", *aliases) {
        handler {
            checkPlayer(sender) {
                GuiManager.startInventory(this) {
                    Navigator(YumeMainMenuScreen())
                }
            }
        }
    }
}