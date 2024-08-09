package ink.pmc.essentials.commands.home

import ink.pmc.essentials.*
import ink.pmc.essentials.api.Essentials
import ink.pmc.essentials.commands.checkPlayer
import ink.pmc.essentials.screens.HomeViewerScreen
import ink.pmc.interactive.inventory.canvas.inv
import ink.pmc.utils.annotation.Command
import ink.pmc.utils.chat.NO_PERMISSON
import ink.pmc.utils.chat.replace
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import ink.pmc.utils.player.uuidOrNull
import kotlin.jvm.optionals.getOrNull

@Command("home")
@Suppress("UNUSED")
fun Cm.home(aliases: Array<String>) {
    this("home", *aliases) {
        permission("essentials.home")
        argument(homes("name").optional())
        handler {
            checkPlayer(sender.sender) {
                val manager = Essentials.homeManager
                val name = optional<String>("name").getOrNull()
                val argUuid = name?.uuidOrNull

                if (argUuid != null) {
                    val uuidHome = manager.get(argUuid)
                    if (uuidHome?.owner != this && !hasPermission("essentials.home.other")) {
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

                if (name == null) {
                    val preferred = manager.getPreferredHome(this)
                    if (preferred == null) {
                        inv { HomeViewerScreen(this, this) }
                        playSound(VIEWER_PAGING_SOUND)
                        return@checkPlayer
                    }
                    preferred.teleportSuspend(this)
                    sendMessage(COMMAND_HOME_SUCCEED.replace("<name>", preferred.name))
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