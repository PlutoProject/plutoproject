package ink.pmc.essentials.commands

import ink.pmc.essentials.COMMAND_ESS_RTP
import ink.pmc.essentials.Cm
import ink.pmc.essentials.Command
import ink.pmc.utils.dsl.cloud.alias
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender

@Command("essentials")
@Suppress("UNUSED")
fun Cm.essentials(aliases: Array<String>) {
    this("essentials", *aliases) {
        permission("essentials.cmd")
        ("randomteleport" alias "rtp") {
            handler {
                sender.sender.sendMessage(COMMAND_ESS_RTP)
            }
        }
    }
}