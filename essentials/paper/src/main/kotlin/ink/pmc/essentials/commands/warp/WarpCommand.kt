package ink.pmc.essentials.commands.warp

import ink.pmc.essentials.*
import ink.pmc.essentials.api.Essentials
import ink.pmc.essentials.commands.checkPlayer
import ink.pmc.utils.annotation.Command
import ink.pmc.utils.chat.replace
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import ink.pmc.utils.player.uuidOrNull

@Command("warp")
@Suppress("UNUSED")
fun Cm.warp(aliases: Array<String>) {
    this("warp", *aliases) {
        permission("essentials.warp")
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

                warp.teleportSuspend(this)
                if (warp.alias == null) {
                    sendMessage(COMMAND_WARP_SUCCEED.replace("<name>", name))
                } else {
                    sendMessage(
                        COMMAND_WARP_SUCCEED_ALIAS
                            .replace("<name>", name)
                            .replace("<alias>", warp.alias!!)
                    )
                }
            }
        }
    }
}