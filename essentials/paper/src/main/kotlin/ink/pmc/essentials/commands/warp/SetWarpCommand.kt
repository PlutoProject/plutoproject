package ink.pmc.essentials.commands.warp

import ink.pmc.essentials.*
import ink.pmc.essentials.api.Essentials
import ink.pmc.essentials.commands.checkPlayer
import ink.pmc.utils.annotation.Command
import ink.pmc.utils.chat.isValidIdentifier
import ink.pmc.utils.chat.replace
import ink.pmc.utils.concurrent.submitAsync
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import org.incendo.cloud.parser.standard.StringParser
import kotlin.jvm.optionals.getOrNull

@Command("setwarp")
@Suppress("UNUSED")
fun Cm.setwarp(aliases: Array<String>) {
    this("setwarp", *aliases) {
        permission("essentials.setwarp")
        required("name", StringParser.quotedStringParser())
        optional("alias", StringParser.quotedStringParser())
        handler {
            checkPlayer(sender.sender) {
                val manager = Essentials.warpManager
                val name = get<String>("name")
                val alias = optional<String>("alias").getOrNull()

                if (manager.has(name)) {
                    sendMessage(COMMAND_SETWARP_FAILED_EXISTED.replace("<name>", name))
                    playSound(TELEPORT_FAILED_SOUND)
                    return@checkPlayer
                }

                if (!name.isValidIdentifier) {
                    sendMessage(COMMAND_SETWARP_FAILED_NOT_VALID)
                    playSound(TELEPORT_FAILED_SOUND)
                    return@checkPlayer
                }

                if (name.length > manager.nameLengthLimit) {
                    sendMessage(COMMAND_SETWARP_FAILED_LENGTN_LIMIT)
                    playSound(TELEPORT_FAILED_SOUND)
                    return@checkPlayer
                }

                submitAsync { manager.create(name, location, alias) }
                if (alias == null) {
                    sendMessage(COMMAND_SETWARP_SUCCEED.replace("<name>", name))
                } else {
                    sendMessage(
                        COMMAND_SETWARP_SUCCEED_ALIAS
                            .replace("<name>", name)
                            .replace("<alias>", alias)
                    )
                }
                playSound(TELEPORT_REQUEST_RECEIVED_SOUND)
            }
        }
    }
}