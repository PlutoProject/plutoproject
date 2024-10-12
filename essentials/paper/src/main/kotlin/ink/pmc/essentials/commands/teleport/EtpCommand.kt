package ink.pmc.essentials.commands.teleport

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.text
import ink.pmc.essentials.COMMAND_ETP_SUCCEED
import ink.pmc.essentials.COMMAND_ETP_SUCCEED_OTHER
import ink.pmc.essentials.TELEPORT_SUCCEED_SOUND
import ink.pmc.essentials.api.Essentials
import ink.pmc.utils.BukkitCommandManager
import ink.pmc.utils.annotation.Command
import ink.pmc.utils.chat.NON_PLAYER
import ink.pmc.utils.chat.replace
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import ink.pmc.utils.visual.mochaText
import org.bukkit.Location
import org.bukkit.entity.Player
import org.incendo.cloud.bukkit.parser.PlayerParser
import org.incendo.cloud.bukkit.parser.location.LocationParser
import org.incendo.cloud.parser.standard.BooleanParser
import kotlin.jvm.optionals.getOrNull

@Command("etp")
@Suppress("UNUSED")
fun BukkitCommandManager.etp(aliases: Array<String>) {
    this("etp", *aliases) {
        permission("essentials.etp")
        required("location", LocationParser.locationParser())
        optional("player", PlayerParser.playerParser())
        optional("bypassSafe", BooleanParser.booleanParser())
        handler {
            val manager = Essentials.teleportManager
            val location = get<Location>("location")
            val argPlayer = optional<Player>("player").getOrNull()
            val sender = sender
            val bypassSafe = optional<Boolean>("bypassSafe").getOrNull() == true
            val options = manager.getWorldTeleportOptions(location.world).copy(disableSafeCheck = bypassSafe)

            if (argPlayer != null) {
                manager.teleportSuspend(argPlayer, location, options)
                sender.sendMessage(
                    COMMAND_ETP_SUCCEED_OTHER
                        .replace("<player>", argPlayer.name)
                        .replace("<location>", component {
                            text("${location.x}, ${location.y}, ${location.z}") with mochaText
                        })
                )
                if (sender is Player) {
                    sender.playSound(TELEPORT_SUCCEED_SOUND)
                }
                return@handler
            }

            if (sender !is Player) {
                sender.sendMessage(NON_PLAYER)
                return@handler
            }

            manager.teleportSuspend(sender, location, options)
            sender.sendMessage(
                COMMAND_ETP_SUCCEED
                    .replace("<location>", component {
                        text("${location.x}, ${location.y}, ${location.z}") with mochaText
                    })
            )
        }
    }
}