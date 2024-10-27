package ink.pmc.essentials.commands.teleport.random

import ink.pmc.essentials.COMMAND_RTP_NOT_ENABLED
import ink.pmc.essentials.Cm
import ink.pmc.essentials.RANDOM_TELEPORT_SPECIFIC
import ink.pmc.essentials.api.teleport.random.RandomTeleportManager
import ink.pmc.framework.utils.chat.NO_PERMISSON
import ink.pmc.framework.utils.command.annotation.Command
import ink.pmc.framework.utils.command.ensurePlayerSuspend
import ink.pmc.framework.utils.command.suggestion.PaperPrivilegedSuggestion
import ink.pmc.framework.utils.dsl.cloud.invoke
import ink.pmc.framework.utils.dsl.cloud.sender
import org.bukkit.World
import org.incendo.cloud.bukkit.parser.WorldParser
import kotlin.jvm.optionals.getOrNull

@Command("rtp")
@Suppress("UNUSED")
fun Cm.rtp(aliases: Array<String>) {
    this("rtp", *aliases) {
        permission("essentials.rtp")
        optional(
            "world",
            WorldParser.worldParser(),
            PaperPrivilegedSuggestion.of(WorldParser(), RANDOM_TELEPORT_SPECIFIC)
        )
        handler {
            ensurePlayerSuspend(sender.sender) {
                val argWorld = optional<World>("world").getOrNull()
                val world = argWorld ?: world
                val perm = !hasPermission(RANDOM_TELEPORT_SPECIFIC) || !hasPermission("essentials.rtp.${world.name}")

                if (argWorld != null && perm) {
                    sendMessage(NO_PERMISSON)
                    return@ensurePlayerSuspend
                }

                if (!RandomTeleportManager.isEnabled(world)) {
                    sendMessage(COMMAND_RTP_NOT_ENABLED)
                    return@ensurePlayerSuspend
                }

                RandomTeleportManager.launch(this, world)
            }
        }
    }
}