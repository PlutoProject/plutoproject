package ink.pmc.essentials.commands.home

import cafe.adriel.voyager.navigator.Navigator
import ink.pmc.essentials.*
import ink.pmc.essentials.api.home.HomeManager
import ink.pmc.essentials.screens.home.HomeViewerScreen
import ink.pmc.framework.interactive.GuiManager
import ink.pmc.framework.utils.chat.NO_PERMISSON
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.command.annotation.Command
import ink.pmc.framework.utils.command.checkPlayer
import ink.pmc.framework.utils.dsl.cloud.invoke
import ink.pmc.framework.utils.dsl.cloud.sender
import ink.pmc.framework.utils.player.uuidOrNull
import kotlin.jvm.optionals.getOrNull

@Command("home")
@Suppress("UNUSED")
fun Cm.home(aliases: Array<String>) {
    this("home", *aliases) {
        permission("essentials.home")
        argument(homes("name").optional())
        handler {
            checkPlayer(sender.sender) {
                val name = optional<String>("name").getOrNull()
                val argUuid = name?.uuidOrNull

                if (argUuid != null) {
                    val uuidHome = HomeManager.get(argUuid)
                    if (uuidHome?.owner != this && !hasPermission("essentials.home.other")) {
                        sendMessage(NO_PERMISSON)
                        return@checkPlayer
                    }
                    if (!HomeManager.has(argUuid)) {
                        sendMessage(COMMAND_HOME_NOT_EXISTED_UUID)
                        playSound(TELEPORT_FAILED_SOUND)
                        return@checkPlayer
                    }
                    val home = HomeManager.get(argUuid)
                    home?.teleportSuspend(this)
                    sendMessage(COMMAND_HOME_SUCCEED.replace("<name>", home!!.name))
                    return@checkPlayer
                }

                if (name == null) {
                    val preferred = HomeManager.getPreferredHome(this)
                    if (preferred == null) {
                        GuiManager.startInventory(this) {
                            Navigator(HomeViewerScreen(this))
                        }
                        playSound(VIEWER_PAGING_SOUND)
                        return@checkPlayer
                    }
                    preferred.teleportSuspend(this)
                    sendMessage(COMMAND_HOME_SUCCEED.replace("<name>", preferred.name))
                    return@checkPlayer
                }

                if (!HomeManager.has(this, name)) {
                    sendMessage(COMMAND_HOME_NOT_EXISTED.replace("<name>", name))
                    playSound(TELEPORT_FAILED_SOUND)
                    return@checkPlayer
                }

                val home = HomeManager.get(this, name)
                home?.teleportSuspend(this)
                sendMessage(COMMAND_HOME_SUCCEED.replace("<name>", name))
            }
        }
    }
}