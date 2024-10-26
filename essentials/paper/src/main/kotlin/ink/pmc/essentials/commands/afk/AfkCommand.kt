package ink.pmc.essentials.commands.afk

import ink.pmc.essentials.Cm
import ink.pmc.essentials.api.afk.AfkManager
import ink.pmc.framework.utils.command.annotation.Command
import ink.pmc.framework.utils.command.checkPlayer
import ink.pmc.framework.utils.dsl.cloud.invoke
import ink.pmc.framework.utils.dsl.cloud.sender

@Command("afk")
@Suppress("UNUSED")
fun Cm.afk(aliases: Array<String>) {
    this("afk", *aliases) {
        permission("essentials.afk")
        handler {
            checkPlayer(sender.sender) {
                AfkManager.toggle(this, true)
            }
        }
    }
}