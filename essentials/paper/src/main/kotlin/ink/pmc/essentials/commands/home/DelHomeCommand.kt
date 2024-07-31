package ink.pmc.essentials.commands.home

import ink.pmc.essentials.*
import ink.pmc.essentials.api.Essentials
import ink.pmc.essentials.commands.checkPlayer
import ink.pmc.utils.chat.replace
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import org.incendo.cloud.parser.standard.StringParser

@Command("delhome")
@Suppress("UNUSED")
fun Cm.delhome(aliases: Array<String>) {
    this("delhome", *aliases) {
        permission("essentials.delhome")
        required("name", StringParser.stringParser())
        handler {
            checkPlayer(sender.sender) {
                val manager = Essentials.homeManager
                val list = manager.list(this)
                val name = get<String>("name")

                if (!list.any { it.name == name }) {
                    sendMessage(COMMAND_HOME_NOT_EXISTED)
                    playSound(TELEPORT_FAILED_SOUND)
                    return@checkPlayer
                }

                manager.remove(this , name)
                sendMessage(COMMAND_DELHOME_SUCCEED.replace("<name>", name))
                playSound(TELEPORT_REQUEST_RECEIVED_SOUND)
            }
        }
    }
}