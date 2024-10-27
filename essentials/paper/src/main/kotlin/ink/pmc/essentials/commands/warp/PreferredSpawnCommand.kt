package ink.pmc.essentials.commands.warp

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.text
import ink.pmc.essentials.COMMAND_PREFERRED_SPAWN_FAILED_ALREADY
import ink.pmc.essentials.COMMAND_PREFERRED_SPAWN_SUCCEED
import ink.pmc.essentials.COMMAND_PREFERRED_SPAWN_WARP_IS_NOT_SPAWN
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.command.ensurePlayer
import ink.pmc.framework.utils.visual.mochaSubtext0
import ink.pmc.framework.utils.visual.mochaText
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.suggestion.Suggestions
import org.incendo.cloud.context.CommandContext
import org.incendo.cloud.context.CommandInput

@Suppress("UNUSED", "UNUSED_PARAMETER", "UnusedReceiverParameter")
object PreferredSpawnCommand {
    @Suggestions("spawn")
    suspend fun CommandContext<CommandSender>.spawn(input: CommandInput): List<String> {
        return WarpManager.listSpawns().map {
            val name = it.name
            val alias = it.alias
            if (alias == null) name else "$name-$alias"
        }
    }

    @Command("preferredspawn <warp>")
    @Permission("essentials.defaultspawn")
    suspend fun CommandSender.preferredSpawn(@Argument("warp", suggestions = "spawn") name: String) = ensurePlayer {
        val warp = parseAndCheck(name) ?: return
        if (!warp.isSpawn) {
            sendMessage(COMMAND_PREFERRED_SPAWN_WARP_IS_NOT_SPAWN.replace("<name>", name))
            return
        }
        val current = WarpManager.getPreferredSpawn(this)
        if (current?.name == warp.name) {
            sendMessage(
                COMMAND_PREFERRED_SPAWN_FAILED_ALREADY.replace("<name>", if (warp.alias != null) component {
                    text("${warp.alias} ") with mochaText
                    text("(${warp.name})") with mochaSubtext0
                } else Component.text(warp.name)))
            return
        }
        WarpManager.setPreferredSpawn(this, warp)
        sendMessage(
            COMMAND_PREFERRED_SPAWN_SUCCEED.replace("<name>", if (warp.alias != null) component {
                text("${warp.alias} ") with mochaText
                text("(${warp.name})") with mochaSubtext0
            } else Component.text(warp.name)))
    }
}