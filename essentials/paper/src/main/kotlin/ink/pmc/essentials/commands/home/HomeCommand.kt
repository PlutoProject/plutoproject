package ink.pmc.essentials.commands.home

import ink.pmc.essentials.COMMAND_HOME_SUCCEED
import ink.pmc.essentials.VIEWER_PAGING_SOUND
import ink.pmc.essentials.api.home.Home
import ink.pmc.essentials.api.home.HomeManager
import ink.pmc.essentials.screens.home.HomeListScreen
import ink.pmc.framework.interactive.GuiManager
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.command.ensurePlayer
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
object HomeCommand {
    @Command("home [home]")
    @Permission("essentials.home")
    suspend fun CommandSender.home(@Argument("home", parserName = "home") home: Home?) = ensurePlayer {
        if (home == null) {
            val preferred = HomeManager.getPreferredHome(this)
            if (preferred == null) {
                GuiManager.startScreen(this, HomeListScreen(this))
                playSound(VIEWER_PAGING_SOUND)
                return
            }
            preferred.teleportSuspend(this)
            sendMessage(COMMAND_HOME_SUCCEED.replace("<name>", preferred.name))
            return
        }
        home.teleportSuspend(this)
        sendMessage(COMMAND_HOME_SUCCEED.replace("<name>", home.name))
    }
}