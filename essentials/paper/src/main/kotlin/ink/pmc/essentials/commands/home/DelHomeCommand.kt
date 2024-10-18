package ink.pmc.essentials.commands.home

import ink.pmc.essentials.*
import ink.pmc.essentials.api.Essentials
import ink.pmc.utils.annotation.Command
import ink.pmc.utils.chat.NO_PERMISSON
import ink.pmc.utils.chat.replace
import ink.pmc.utils.command.checkPlayer
import ink.pmc.utils.concurrent.submitAsync
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import ink.pmc.utils.player.uuidOrNull

@Command("delhome")
@Suppress("UNUSED")
fun Cm.delhome(aliases: Array<String>) {
    this("delhome", *aliases) {
        permission("essentials.delhome")
        argument(homes("name").required())
        handler {
            checkPlayer(sender.sender) {
                val manager = Essentials.homeManager
                val name = get<String>("name")
                val argUuid = name.uuidOrNull

                if (argUuid != null) {
                    val uuidHome = manager.get(argUuid)
                    if (uuidHome?.owner != this && !hasPermission("essentials.delhome.other")) {
                        sendMessage(NO_PERMISSON)
                        return@checkPlayer
                    }
                    if (!manager.has(argUuid)) {
                        sendMessage(COMMAND_HOME_NOT_EXISTED_UUID)
                        playSound(TELEPORT_FAILED_SOUND)
                        return@checkPlayer
                    }
                    val home = manager.get(argUuid)
                    submitAsync { manager.remove(argUuid) }
                    sendMessage(COMMAND_HOME_SUCCEED.replace("<name>", home!!.name))
                    return@checkPlayer
                }

                if (!manager.has(this, name)) {
                    sendMessage(COMMAND_HOME_NOT_EXISTED.replace("<name>", name))
                    playSound(TELEPORT_FAILED_SOUND)
                    return@checkPlayer
                }

                submitAsync { manager.remove(this@checkPlayer, name) }
                sendMessage(COMMAND_DELHOME_SUCCEED.replace("<name>", name))
                playSound(TELEPORT_REQUEST_RECEIVED_SOUND)
            }
        }
    }
}