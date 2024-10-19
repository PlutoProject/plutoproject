package ink.pmc.essentials.commands

import ink.pmc.essentials.COMMAND_ALIGN_POS_SUCCEED
import ink.pmc.essentials.COMMAND_ALIGN_SUCCEED
import ink.pmc.essentials.COMMAND_ALIGN_VIEW_SUCCEED
import ink.pmc.essentials.Cm
import ink.pmc.utils.command.annotation.Command
import ink.pmc.utils.command.checkPlayer
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import ink.pmc.utils.entity.teleportSuspend
import org.bukkit.block.BlockFace.*
import org.bukkit.entity.Player

@Command("align")
@Suppress("UNUSED")
fun Cm.align(aliases: Array<String>) {
    this("align", *aliases) {
        permission("essentials.align")
        handler {
            checkPlayer(sender.sender) {
                alignPos()
                alignView()
                sendMessage(COMMAND_ALIGN_SUCCEED)
            }
        }

        "pos" {
            permission("essentials.align.pos")
            handler {
                checkPlayer(sender.sender) {
                    alignPos()
                    sendMessage(COMMAND_ALIGN_POS_SUCCEED)
                }
            }
        }

        "view" {
            permission("essentials.align.view")
            handler {
                checkPlayer(sender.sender) {
                    alignView()
                    sendMessage(COMMAND_ALIGN_VIEW_SUCCEED)
                }
            }
        }
    }
}

private suspend fun Player.alignPos() {
    teleportSuspend(location.toCenterLocation())
}

private suspend fun Player.alignView() {
    val alignYaw = when (facing) {
        NORTH -> -180.0F
        EAST -> -90.0F
        SOUTH -> 0.0F
        WEST -> 90.0F
        NORTH_EAST -> -180.0F
        NORTH_WEST -> -180.0F
        SOUTH_EAST -> 0.0F
        SOUTH_WEST -> 0.0F
        WEST_NORTH_WEST -> 90.0F
        NORTH_NORTH_WEST -> -180.0F
        NORTH_NORTH_EAST -> -180.0F
        EAST_NORTH_EAST -> -90.0F
        EAST_SOUTH_EAST -> -90.0F
        SOUTH_SOUTH_EAST -> 0.0F
        SOUTH_SOUTH_WEST -> 0.0F
        WEST_SOUTH_WEST -> 90.0F
        else -> 0.0F
    }
    val loc = location.clone().apply {
        yaw = alignYaw
        pitch = 0.0F
    }
    teleportSuspend(loc)
}