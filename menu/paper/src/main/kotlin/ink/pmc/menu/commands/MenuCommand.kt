package ink.pmc.menu.commands

import ink.pmc.framework.startScreen
import ink.pmc.framework.utils.command.ensurePlayer
import ink.pmc.menu.screens.NotebookScreen
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.Command

@Suppress("UNUSED")
object MenuCommand {
    @Command("menu")
    fun CommandSender.menu() = ensurePlayer {
        startScreen(NotebookScreen())
    }
}