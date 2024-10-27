package ink.pmc.essentials.commands.warp

import cafe.adriel.voyager.navigator.Navigator
import ink.pmc.essentials.*
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.essentials.screens.warp.WarpViewerScreen
import ink.pmc.framework.interactive.GuiManager
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.command.annotation.Command
import ink.pmc.framework.utils.command.ensurePlayerSuspend
import ink.pmc.framework.utils.dsl.cloud.invoke
import ink.pmc.framework.utils.dsl.cloud.sender
import ink.pmc.framework.utils.player.uuidOrNull
import kotlin.jvm.optionals.getOrNull

@Command("warp")
@Suppress("UNUSED")
fun Cm.warp(aliases: Array<String>) {
    this("warp", *aliases) {
        permission("essentials.warp")
        argument(warps("name").optional())
        handler {
            ensurePlayerSuspend(sender.sender) {
                val input = optional<String>("name").getOrNull()

                if (input == null) {
                    GuiManager.startInventory(this) {
                        Navigator(WarpViewerScreen())
                    }
                    playSound(VIEWER_PAGING_SOUND)
                    return@ensurePlayerSuspend
                }

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