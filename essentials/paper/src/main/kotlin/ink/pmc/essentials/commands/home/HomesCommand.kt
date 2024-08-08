package ink.pmc.essentials.commands.home

import cafe.adriel.voyager.navigator.Navigator
import ink.pmc.essentials.Cm
import ink.pmc.essentials.HOMES_OTHER
import ink.pmc.essentials.commands.checkPlayer
import ink.pmc.essentials.screens.HomeViewerScreen
import ink.pmc.interactive.inventory.canvas.inv
import ink.pmc.utils.annotation.Command
import ink.pmc.utils.chat.NO_PERMISSON
import ink.pmc.utils.command.suggestion.PaperPrivilegedSuggestion
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import org.bukkit.OfflinePlayer
import org.incendo.cloud.bukkit.parser.OfflinePlayerParser
import org.incendo.cloud.bukkit.parser.WorldParser
import kotlin.jvm.optionals.getOrNull

@Command("homes")
@Suppress("UNUSED")
fun Cm.homes(aliases: Array<String>) {
    this("homes", *aliases) {
        permission("essentials.homes")
        optional(
            "player",
            OfflinePlayerParser.offlinePlayerParser(),
            PaperPrivilegedSuggestion.of(WorldParser(), HOMES_OTHER)
        )
        handler {
            checkPlayer(sender.sender) {
                val argPlayer = optional<OfflinePlayer>("player").getOrNull()

                if (argPlayer != null) {
                    if (!hasPermission(HOMES_OTHER)) {
                        sendMessage(NO_PERMISSON)
                        return@checkPlayer
                    }
                    inv {
                        Navigator(HomeViewerScreen(this, argPlayer))
                    }
                }

                inv {
                    Navigator(HomeViewerScreen(this, this))
                }
            }
        }
    }
}