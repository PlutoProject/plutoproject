package ink.pmc.menu.commands

import cafe.adriel.voyager.navigator.Navigator
import ink.pmc.interactive.api.GuiManager
import ink.pmc.menu.screens.MainMenuScreen
import ink.pmc.utils.PaperCm
import ink.pmc.utils.command.annotation.Command
import ink.pmc.utils.command.checkPlayer
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender

@Command("menu")
@Suppress("UNUSED")
fun PaperCm.menu(aliases: Array<String>) {
    this("menu", *aliases) {
        handler {
            checkPlayer(sender.sender) {
                GuiManager.startInventory(this) {
                    Navigator(MainMenuScreen())
                }
            }
        }
    }
}