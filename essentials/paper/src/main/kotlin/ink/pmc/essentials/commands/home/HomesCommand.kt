package ink.pmc.essentials.commands.home

import cafe.adriel.voyager.navigator.Navigator
import ink.pmc.essentials.Cm
import ink.pmc.essentials.commands.checkPlayer
import ink.pmc.essentials.screens.HomeViewerScreen
import ink.pmc.interactive.inventory.canvas.inv
import ink.pmc.utils.annotation.Command
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import org.incendo.cloud.bukkit.parser.PlayerParser

@Command("homes")
@Suppress("UNUSED")
fun Cm.homes(aliases: Array<String>) {
    this("homes", *aliases) {
        permission("essentials.homes")
        optional("player", PlayerParser.playerParser())
        handler {
            checkPlayer(sender.sender) {
                inv {
                    Navigator(HomeViewerScreen(this, this))
                }
            }
        }
    }
}