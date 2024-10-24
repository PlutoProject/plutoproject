package ink.pmc.essentials.commands.home

import ink.pmc.essentials.*
import ink.pmc.essentials.api.Essentials
import ink.pmc.framework.utils.command.annotation.Command
import ink.pmc.framework.utils.chat.isValidIdentifier
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.command.checkPlayer
import ink.pmc.framework.utils.concurrent.submitAsync
import ink.pmc.framework.utils.dsl.cloud.invoke
import ink.pmc.framework.utils.dsl.cloud.sender
import org.incendo.cloud.parser.standard.StringParser
import kotlin.jvm.optionals.getOrNull

@Command("sethome")
@Suppress("UNUSED")
fun Cm.sethome(aliases: Array<String>) {
    this("sethome", *aliases) {
        permission("essentials.sethome")
        optional("name", StringParser.quotedStringParser())
        handler {
            checkPlayer(sender.sender) {
                val manager = Essentials.homeManager
                val list = manager.list(this)
                val name = optional<String>("name").getOrNull() ?: "home"

                if (list.size >= manager.maxHomes && !hasPermission(BYPASS_HOME_LIMIT)) {
                    sendMessage(COMMAND_SETHOME_FAILED_REACH_LIMIT)
                    playSound(TELEPORT_FAILED_SOUND)
                    return@checkPlayer
                }

                if (manager.has(this, name)) {
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

                submitAsync { manager.create(this@checkPlayer, name, location) }
                sendMessage(COMMAND_SETHOME_SUCCEED.replace("<name>", name))
                playSound(TELEPORT_REQUEST_RECEIVED_SOUND)
            }
        }
    }
}