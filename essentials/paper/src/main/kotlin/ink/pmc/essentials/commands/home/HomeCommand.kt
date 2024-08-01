package ink.pmc.essentials.commands.home

import ink.pmc.essentials.*
import ink.pmc.essentials.api.Essentials
import ink.pmc.essentials.commands.checkPlayer
import ink.pmc.utils.chat.NO_PERMISSON
import ink.pmc.utils.chat.replace
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import ink.pmc.utils.player.uuidOrNull

@Command("home")
@Suppress("UNUSED")
fun Cm.home(aliases: Array<String>) {
    this("home", *aliases) {
        permission("essentials.home")
        argument(homes("name").required())
        handler {
            checkPlayer(sender.sender) {
                val manager = Essentials.homeManager
                val name = get<String>("name")
                val argUuid = name.uuidOrNull

                if (argUuid != null) {
                    if (!hasPermission("essentials.home.other")) {
                        sendMessage(NO_PERMISSON)
                        return@checkPlayer
                    }
                    if (!manager.has(argUuid)) {
                        sendMessage(COMMAND_HOME_NOT_EXISTED_UUID)
                        playSound(TELEPORT_FAILED_SOUND)
                        return@checkPlayer
                    }
                    val home = manager.get(argUuid)
                    home?.teleportSuspend(this)
                    sendMessage(COMMAND_HOME_SUCCEED.replace("<name>", home!!.name))
                    return@checkPlayer
                }

                if (!manager.has(this, name)) {
                    sendMessage(COMMAND_HOME_NOT_EXISTED.replace("<name>", name))
                    playSound(TELEPORT_FAILED_SOUND)
                    return@checkPlayer
                }

                val home = manager.get(this, name)
                home?.teleportSuspend(this)
                sendMessage(COMMAND_HOME_SUCCEED.replace("<name>", name))
            }
        }
    }
}