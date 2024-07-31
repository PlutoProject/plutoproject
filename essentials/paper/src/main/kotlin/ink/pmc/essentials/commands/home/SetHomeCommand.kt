package ink.pmc.essentials.commands.home

import ink.pmc.essentials.*
import ink.pmc.essentials.api.Essentials
import ink.pmc.essentials.commands.checkPlayer
import ink.pmc.utils.chat.isValidIdentifier
import ink.pmc.utils.chat.replace
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import org.incendo.cloud.parser.standard.StringParser

@Command("sethome")
@Suppress("UNUSED")
fun Cm.sethome(aliases: Array<String>) {
    this("sethome", *aliases) {
        permission("essentials.sethome")
        required("name", StringParser.stringParser())
        handler {
            checkPlayer(sender.sender) {
                val manager = Essentials.homeManager
                val list = manager.list(this)
                val name = get<String>("name")

                if (list.size >= manager.maxHomes && !hasPermission(BYPASS_HOME_LIMIT)) {
                    sendMessage(COMMAND_SETHOME_FAILED_REACH_LIMIT)
                    playSound(TELEPORT_FAILED_SOUND)
                    return@checkPlayer
                }

                if (list.any { it.name == name }) {
                    sendMessage(COMMAND_SETHOME_FAILED_EXISTED.replace("<name>", name))
                    playSound(TELEPORT_FAILED_SOUND)
                    return@checkPlayer
                }

                if (!name.isValidIdentifier) {
                    sendMessage(COMMAND_SETHOME_FAILED_NOT_VALID)
                    playSound(TELEPORT_FAILED_SOUND)
                    return@checkPlayer
                }

                if (name.length > manager.nameLengthLimit) {
                    sendMessage(COMMAND_SETHOME_FAILED_LENGTN_LIMIT)
                    playSound(TELEPORT_FAILED_SOUND)
                    return@checkPlayer
                }

                manager.create(this, name, location)
                sendMessage(COMMAND_SETHOME_SUCCEED.replace("<name>", name))
                playSound(TELEPORT_REQUEST_RECEIVED_SOUND)
            }
        }
    }
}