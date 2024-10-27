package ink.pmc.essentials.commands.home

import ink.pmc.essentials.*
import ink.pmc.essentials.api.home.HomeManager
import ink.pmc.framework.utils.chat.NO_PERMISSON
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.command.annotation.Command
import ink.pmc.framework.utils.command.ensurePlayerSuspend
import ink.pmc.framework.utils.concurrent.submitAsync
import ink.pmc.framework.utils.dsl.cloud.invoke
import ink.pmc.framework.utils.dsl.cloud.sender
import ink.pmc.framework.utils.player.uuidOrNull

@Command("delhome")
@Suppress("UNUSED")
fun Cm.delhome(aliases: Array<String>) {
    this("delhome", *aliases) {
        permission("essentials.delhome")
        argument(homes("name").required())
        handler {
            ensurePlayerSuspend(sender.sender) {
                val name = get<String>("name")
                val argUuid = name.uuidOrNull

                if (argUuid != null) {
                    val uuidHome = HomeManager.get(argUuid)
                    if (uuidHome?.owner != this && !hasPermission("essentials.delhome.other")) {
                        sendMessage(NO_PERMISSON)
                        return@ensurePlayerSuspend
                    }
                    if (!HomeManager.has(argUuid)) {
                        sendMessage(COMMAND_HOME_NOT_EXISTED_UUID)
                        playSound(TELEPORT_FAILED_SOUND)
                        return@ensurePlayerSuspend
                    }
                    val home = HomeManager.get(argUuid)
                    submitAsync { HomeManager.remove(argUuid) }
                    sendMessage(COMMAND_HOME_SUCCEED.replace("<name>", home!!.name))
                    return@ensurePlayerSuspend
                }

                if (!HomeManager.has(this, name)) {
                    sendMessage(COMMAND_HOME_NOT_EXISTED.replace("<name>", name))
                    playSound(TELEPORT_FAILED_SOUND)
                    return@ensurePlayerSuspend
                }

                submitAsync { HomeManager.remove(this@checkPlayer, name) }
                sendMessage(COMMAND_DELHOME_SUCCEED.replace("<name>", name))
                playSound(TELEPORT_REQUEST_RECEIVED_SOUND)
            }
        }
    }
}