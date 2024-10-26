package ink.pmc.essentials.commands.home

import cafe.adriel.voyager.navigator.Navigator
import ink.pmc.essentials.Cm
import ink.pmc.essentials.HOMES_OTHER
import ink.pmc.essentials.TELEPORT_FAILED_SOUND
import ink.pmc.essentials.VIEWER_PAGING_SOUND
import ink.pmc.essentials.api.home.HomeManager
import ink.pmc.essentials.screens.home.HomeViewerScreen
import ink.pmc.framework.interactive.GuiManager
import ink.pmc.framework.utils.chat.NO_PERMISSON
import ink.pmc.framework.utils.chat.PLAYER_HAS_NO_HOME
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.command.annotation.Command
import ink.pmc.framework.utils.command.checkPlayer
import ink.pmc.framework.utils.command.suggestion.PaperPrivilegedSuggestion
import ink.pmc.framework.utils.dsl.cloud.invoke
import ink.pmc.framework.utils.dsl.cloud.sender
import org.bukkit.OfflinePlayer
import org.incendo.cloud.bukkit.parser.OfflinePlayerParser
import org.koin.java.KoinJavaComponent.getKoin
import kotlin.jvm.optionals.getOrNull

@Command("homes")
@Suppress("UNUSED")
fun Cm.homes(aliases: Array<String>) {
    this("homes", *aliases) {
        permission("essentials.homes")
        optional(
            "player",
            OfflinePlayerParser.offlinePlayerParser(),
            PaperPrivilegedSuggestion.of(OfflinePlayerParser(), HOMES_OTHER)
        )
        handler {
            checkPlayer(sender.sender) {
                val manager = getKoin().get<HomeManager>()
                val argPlayer = optional<OfflinePlayer>("player").getOrNull()

                if (argPlayer != null) {
                    if (!hasPermission(HOMES_OTHER)) {
                        sendMessage(NO_PERMISSON)
                        playSound(TELEPORT_FAILED_SOUND)
                        return@checkPlayer
                    }

                    if (!manager.hasHome(argPlayer)) {
                        sendMessage(
                            PLAYER_HAS_NO_HOME.replace(
                                "<player>",
                                argPlayer.name ?: argPlayer.uniqueId
                            )
                        )
                        playSound(TELEPORT_FAILED_SOUND)
                        return@checkPlayer
                    }

                    GuiManager.startInventory(this) {
                        Navigator(HomeViewerScreen(argPlayer))
                    }

                    playSound(VIEWER_PAGING_SOUND)
                    return@checkPlayer
                }

                GuiManager.startInventory(this) {
                    Navigator(HomeViewerScreen(this))
                }
                playSound(VIEWER_PAGING_SOUND)
            }
        }
    }
}