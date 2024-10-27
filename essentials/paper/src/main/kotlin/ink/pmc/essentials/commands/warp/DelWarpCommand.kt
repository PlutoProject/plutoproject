package ink.pmc.essentials.commands.warp

import ink.pmc.essentials.*
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.concurrent.submitAsync
import ink.pmc.framework.utils.player.uuidOrNull
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
object DelWarpCommand {
    @Command("delwarp <warp>")
    @Permission("essentials.delwarp")
    suspend fun CommandSender.delwarp(@Argument("warp", suggestions = "warps") warp: String) {
        val argUuid = warp.uuidOrNull
        val actualWarp = if (argUuid != null) {
            WarpManager.get(argUuid)
        } else {
            WarpManager.get(warp)
        }
        if (actualWarp == null && argUuid != null) {
            sendMessage(COMMAND_WARP_FAILED_NOT_EXISTED_UUID)
            playSound(TELEPORT_FAILED_SOUND)
            return
        }
        if (actualWarp == null) {
            sendMessage(COMMAND_WARP_NOT_EXISTED.replace("<name>", warp))
            playSound(TELEPORT_FAILED_SOUND)
            return
        }
        submitAsync {
            WarpManager.remove(actualWarp.id)
        }
        if (actualWarp.alias == null) {
            sendMessage(COMMAND_DELWARP_SUCCEED.replace("<name>", warp))
        } else {
            sendMessage(
                COMMAND_DELWARP_SUCCEED_ALIAS
                    .replace("<name>", warp)
                    .replace("<alias>", actualWarp.alias!!)
            )
        }
        playSound(TELEPORT_REQUEST_RECEIVED_SOUND)
    }
}