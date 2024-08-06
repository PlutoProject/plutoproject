package ink.pmc.essentials.commands.teleport.random

import ink.pmc.essentials.COMMAND_RTP_NOT_ENABLED
import ink.pmc.essentials.Cm
import ink.pmc.essentials.api.Essentials
import ink.pmc.essentials.commands.checkPlayer
import ink.pmc.utils.annotation.Command
import ink.pmc.utils.chat.NO_PERMISSON
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import org.bukkit.World
import org.incendo.cloud.bukkit.parser.WorldParser
import kotlin.jvm.optionals.getOrNull

@Command("rtp")
@Suppress("UNUSED")
fun Cm.rtp(aliases: Array<String>) {
    this("rtp", *aliases) {
        permission("essentials.rtp")
        optional("world", WorldParser.worldParser())
        handler {
            checkPlayer(sender.sender) {
                val manager = Essentials.randomTeleportManager
                val argWorld = optional<World>("world").getOrNull()
                val world = argWorld ?: world

                if (argWorld != null && !hasPermission("essentials.rtp.${world.name}")) {
                    sendMessage(NO_PERMISSON)
                    return@checkPlayer
                }

                if (!manager.isEnabled(world)) {
                    sendMessage(COMMAND_RTP_NOT_ENABLED)
                    return@checkPlayer
                }

                manager.launch(this, world)
            }
        }
    }
}