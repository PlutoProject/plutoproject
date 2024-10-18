package ink.pmc.essentials.commands.warp

import ink.pmc.essentials.*
import ink.pmc.essentials.api.Essentials
import ink.pmc.utils.annotation.Command
import ink.pmc.utils.chat.replace
import ink.pmc.utils.command.checkPlayer
import ink.pmc.utils.concurrent.submitAsync
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import ink.pmc.utils.player.uuidOrNull

@Command("delwarp")
@Suppress("UNUSED")
fun Cm.delwarp(aliases: Array<String>) {
    this("delwarp", *aliases) {
        permission("essentials.delwarp")
        argument(warps("name").required())
        handler {
            checkPlayer(sender.sender) {
                val manager = Essentials.warpManager
                val input = get<String>("name")
                val name = input.substringBefore('-')
                val argUuid = name.uuidOrNull

                val warp = if (argUuid != null) {
                    manager.get(argUuid)
                } else {
                    manager.get(name)
                }

                if (warp == null && argUuid != null) {
                    sendMessage(COMMAND_WARP_FAILED_NOT_EXISTED_UUID)
                    playSound(TELEPORT_FAILED_SOUND)
                    return@checkPlayer
                }

                if (warp == null) {
                    sendMessage(COMMAND_WARP_NOT_EXISTED.replace("<name>", name))
                    playSound(TELEPORT_FAILED_SOUND)
                    return@checkPlayer
                }

                submitAsync { manager.remove(warp.id) }
                if (warp.alias == null) {
                    sendMessage(COMMAND_DELWARP_SUCCEED.replace("<name>", name))
                    return@checkPlayer
                } else {
                    sendMessage(
                        COMMAND_DELWARP_SUCCEED_ALIAS
                            .replace("<name>", name)
                            .replace("<alias>", warp.alias!!)
                    )
                }
                playSound(TELEPORT_REQUEST_RECEIVED_SOUND)
            }
        }
    }
}