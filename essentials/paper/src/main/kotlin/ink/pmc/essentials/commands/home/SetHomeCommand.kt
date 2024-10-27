package ink.pmc.essentials.commands.home

import ink.pmc.essentials.*
import ink.pmc.essentials.api.home.HomeManager
import ink.pmc.framework.utils.chat.isValidIdentifier
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.command.annotation.Command
import ink.pmc.framework.utils.command.ensurePlayerSuspend
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
            ensurePlayerSuspend(sender.sender) {
                val list = HomeManager.list(this)
                val name = optional<String>("name").getOrNull() ?: "home"

                if (list.size >= HomeManager.maxHomes && !hasPermission(BYPASS_HOME_LIMIT)) {
                    sendMessage(COMMAND_SETHOME_FAILED_REACH_LIMIT)
                    playSound(TELEPORT_FAILED_SOUND)
                    return@ensurePlayerSuspend
                }

                if (HomeManager.has(this, name)) {
                    sendMessage(COMMAND_SETHOME_FAILED_EXISTED.replace("<name>", name))
                    playSound(TELEPORT_FAILED_SOUND)
                    return@ensurePlayerSuspend
                }

                if (!name.isValidIdentifier) {
                    sendMessage(COMMAND_SETHOME_FAILED_NOT_VALID)
                    playSound(TELEPORT_FAILED_SOUND)
                    return@ensurePlayerSuspend
                }

                if (name.length > HomeManager.nameLengthLimit) {
                    sendMessage(COMMAND_SETHOME_FAILED_LENGTN_LIMIT)
                    playSound(TELEPORT_FAILED_SOUND)
                    return@ensurePlayerSuspend
                }

                submitAsync { HomeManager.create(this@ensurePlayerSuspend, name, location) }
                sendMessage(COMMAND_SETHOME_SUCCEED.replace("<name>", name))
                playSound(TELEPORT_REQUEST_RECEIVED_SOUND)
            }
        }
    }
}