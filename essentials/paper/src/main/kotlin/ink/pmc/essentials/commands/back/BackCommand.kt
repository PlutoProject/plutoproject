package ink.pmc.essentials.commands.back

import ink.pmc.essentials.COMMAND_BACK_FAILED_NO_LOC
import ink.pmc.essentials.COMMAND_BACK_SUCCEED
import ink.pmc.essentials.Cm
import ink.pmc.essentials.TELEPORT_FAILED_SOUND
import ink.pmc.essentials.api.Essentials
import ink.pmc.framework.utils.command.annotation.Command
import ink.pmc.framework.utils.command.checkPlayer
import ink.pmc.framework.utils.dsl.cloud.invoke
import ink.pmc.framework.utils.dsl.cloud.sender

@Command("back")
@Suppress("UNUSED")
fun Cm.back(alias: Array<String>) {
    this("back", *alias) {
        permission("essentials.back")
        handler {
            checkPlayer(sender.sender) {
                val manager = Essentials.backManager

                if (!manager.has(this)) {
                    sendMessage(COMMAND_BACK_FAILED_NO_LOC)
                    playSound(TELEPORT_FAILED_SOUND)
                    return@checkPlayer
                }

                manager.backSuspend(this)
                sendMessage(COMMAND_BACK_SUCCEED)
            }
        }
    }
}