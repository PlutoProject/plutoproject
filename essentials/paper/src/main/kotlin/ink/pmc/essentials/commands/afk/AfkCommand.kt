package ink.pmc.essentials.commands.afk

import ink.pmc.essentials.api.Essentials
import ink.pmc.utils.BukkitCommandManager
import ink.pmc.utils.annotation.Command
import ink.pmc.utils.command.checkPlayer
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender

@Command("afk")
@Suppress("UNUSED")
fun BukkitCommandManager.afk(aliases: Array<String>) {
    this("afk", *aliases) {
        permission("essentials.afk")
        handler {
            checkPlayer(sender) {
                val manager = Essentials.afkManager
                manager.toggle(this, true)
            }
        }
    }
}