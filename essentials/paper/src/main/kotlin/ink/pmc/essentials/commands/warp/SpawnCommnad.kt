package ink.pmc.essentials.commands.warp

import ink.pmc.essentials.*
import ink.pmc.essentials.api.Essentials
import ink.pmc.framework.utils.command.annotation.Command
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.command.checkPlayer
import ink.pmc.framework.utils.dsl.cloud.invoke
import ink.pmc.framework.utils.dsl.cloud.sender

@Command("spawn")
@Suppress("UNUSED")
fun Cm.spawn(aliases: Array<String>) {
    this("spawn", *aliases) {
        permission("essentials.spawn")
        handler {
            checkPlayer(sender.sender) {
                val manager = Essentials.warpManager
                val spawn = manager.getPreferredSpawn(this)

                if (spawn == null) {
                    sendMessage(COMMAND_SPAWN_FAILED_NOT_SET)
                    playSound(TELEPORT_FAILED_SOUND)
                    return@checkPlayer
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