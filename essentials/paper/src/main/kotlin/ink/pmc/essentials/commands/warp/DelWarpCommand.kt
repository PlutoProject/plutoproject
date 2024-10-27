package ink.pmc.essentials.commands.warp

import ink.pmc.essentials.*
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.command.annotation.Command
import ink.pmc.framework.utils.command.ensurePlayerSuspend
import ink.pmc.framework.utils.concurrent.submitAsync
import ink.pmc.framework.utils.dsl.cloud.invoke
import ink.pmc.framework.utils.dsl.cloud.sender
import ink.pmc.framework.utils.player.uuidOrNull

@Command("delwarp")
@Suppress("UNUSED")
fun Cm.delwarp(aliases: Array<String>) {
    this("delwarp", *aliases) {
        permission("essentials.delwarp")
        argument(warps("name").required())
        handler {
            ensurePlayerSuspend(sender.sender) {
                val input = get<String>("name")
                val name = input.substringBefore('-')
                val argUuid = name.uuidOrNull

                val warp = if (argUuid != null) {
                    WarpManager.get(argUuid)
                } else {
                    WarpManager.get(name)
                }

                if (warp == null && argUuid != null) {
                    sendMessage(COMMAND_WARP_FAILED_NOT_EXISTED_UUID)
                    playSound(TELEPORT_FAILED_SOUND)
                    return@ensurePlayerSuspend
                }

                if (warp == null) {
                    sendMessage(COMMAND_WARP_NOT_EXISTED.replace("<name>", name))
                    playSound(TELEPORT_FAILED_SOUND)
                    return@ensurePlayerSuspend
                }

                submitAsync { WarpManager.remove(warp.id) }
                if (warp.alias == null) {
                    sendMessage(COMMAND_DELWARP_SUCCEED.replace("<name>", name))
                    return@ensurePlayerSuspend
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