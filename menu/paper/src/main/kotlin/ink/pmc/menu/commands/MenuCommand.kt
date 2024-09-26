package ink.pmc.menu.commands

import cafe.adriel.voyager.navigator.Navigator
import ink.pmc.interactive.api.gui.Gui
import ink.pmc.menu.screens.YumeMainMenuScreen
import ink.pmc.utils.PaperCm
import ink.pmc.utils.annotation.Command
import ink.pmc.utils.command.checkPlayer
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender

@Command("menu")
@Suppress("UNUSED")
fun PaperCm.menu(aliases: Array<String>) {
    this("menu", *aliases) {
        handler {
            checkPlayer(sender.sender) {
                Gui.startInventory(this) {
                    Navigator(YumeMainMenuScreen())
                }
            }
        }
    }
}