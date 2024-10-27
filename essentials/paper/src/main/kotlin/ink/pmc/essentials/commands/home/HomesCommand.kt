package ink.pmc.essentials.commands.home

import cafe.adriel.voyager.navigator.Navigator
import ink.pmc.essentials.HOMES_OTHER
import ink.pmc.essentials.TELEPORT_FAILED_SOUND
import ink.pmc.essentials.api.home.HomeManager
import ink.pmc.essentials.screens.home.HomeViewerScreen
import ink.pmc.framework.interactive.GuiManager
import ink.pmc.framework.utils.chat.NO_PERMISSON
import ink.pmc.framework.utils.chat.PLAYER_HAS_NO_HOME
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.command.ensurePlayer
import ink.pmc.framework.utils.command.selectPlayer
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
object HomesCommand {
    @Command("homes [player]")
    @Permission("essentials.homes")
    suspend fun CommandSender.homes(
        @Argument("player", suggestions = "homes-offlineplayer") player: OfflinePlayer?
    ) = ensurePlayer {
        val actualPlayer = selectPlayer(this, player)!!
        if (this != actualPlayer) {
            if (!hasPermission(HOMES_OTHER)) {
                sendMessage(NO_PERMISSON)
                playSound(TELEPORT_FAILED_SOUND)
                return
            }
            if (!HomeManager.hasHome(actualPlayer)) {
                sendMessage(
                    PLAYER_HAS_NO_HOME.replace(
                        "<player>",
                        actualPlayer.name ?: actualPlayer.uniqueId
                    )
                )
                playSound(TELEPORT_FAILED_SOUND)
                return
            }
        }
        GuiManager.startInventory(this) {
            Navigator(HomeViewerScreen(actualPlayer))
        }
    }
}