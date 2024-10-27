package ink.pmc.essentials.commands.warp

import ink.pmc.essentials.*
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.command.annotation.Command
import ink.pmc.framework.utils.command.ensurePlayerSuspend
import ink.pmc.framework.utils.dsl.cloud.invoke
import ink.pmc.framework.utils.dsl.cloud.sender

@Command("spawn")
@Suppress("UNUSED")
fun Cm.spawn(aliases: Array<String>) {
    this("spawn", *aliases) {
        permission("essentials.spawn")
        handler {
            ensurePlayerSuspend(sender.sender) {
                val spawn = WarpManager.getPreferredSpawn(this)

                if (spawn == null) {
                    sendMessage(COMMAND_SPAWN_FAILED_NOT_SET)
                    playSound(TELEPORT_FAILED_SOUND)
                    return@ensurePlayerSuspend
                }

                spawn.teleport(this)
                if (spawn.alias == null) {
                    sendMessage(COMMAND_WARP_SUCCEED.replace("<name>", spawn.name))
                } else {
                    sendMessage(
                        COMMAND_WARP_SUCCEED_ALIAS
                            .replace("<name>", spawn.name)
                            .replace("<alias>", spawn.alias)
                    )
                }
            }
        }
    }
}