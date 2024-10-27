package ink.pmc.misc.commands

import ink.pmc.framework.utils.command.ensurePlayerSuspend
import ink.pmc.framework.utils.concurrent.sync
import ink.pmc.misc.SUICIDE
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.Command

@Suppress("UNUSED")
object SuicideCommand {
    @Command("suicide")
    suspend fun CommandSender.suicide() = ensurePlayerSuspend {
        sync {
            this@ensurePlayerSuspend.health = 0.0
            this@ensurePlayerSuspend.sendMessage(SUICIDE)
        }
    }
}