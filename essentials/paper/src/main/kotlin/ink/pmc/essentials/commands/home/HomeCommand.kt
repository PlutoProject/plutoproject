package ink.pmc.essentials.commands.home

import ink.pmc.essentials.*
import ink.pmc.essentials.api.Essentials
import ink.pmc.essentials.commands.checkPlayer
import ink.pmc.utils.chat.replace
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import org.incendo.cloud.parser.standard.StringParser

@Command("home")
@Suppress("UNUSED")
fun Cm.home(aliases: Array<String>) {
    this("home", *aliases) {
        permission("essentials.home")
        required("name", StringParser.stringParser())
        handler {
            checkPlayer(sender.sender) {
                val manager = Essentials.homeManager
                val list = manager.list(this)
                val name = get<String>("name")

                if (!list.any { it.name == name }) {
                    sendMessage(COMMAND_HOME_NOT_EXISTED.replace("<name>", name))
                    playSound(TELEPORT_FAILED_SOUND)
                    return@checkPlayer
                }

                val home = list.first { it.name == name }
                home.teleportSuspend(this)
                sendMessage(COMMAND_HOME_SUCCEED.replace("<name>", name))
            }
        }
    }
}