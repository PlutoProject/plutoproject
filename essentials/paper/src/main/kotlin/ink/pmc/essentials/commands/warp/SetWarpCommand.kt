package ink.pmc.essentials.commands.warp

import ink.pmc.essentials.*
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.framework.utils.chat.isValidIdentifier
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.command.ensurePlayer
import ink.pmc.framework.utils.concurrent.submitAsync
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotation.specifier.Quoted
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
@Permission("essentials.setwarp")
object SetWarpCommand {
    @Command("setwarp <name> [alias]")
    @Permission("essentials.setwarp")
    suspend fun CommandSender.setWarp(
        @Argument("name") @Quoted name: String,
        @Argument("alias") @Quoted alias: String?
    ) = ensurePlayer {
        if (WarpManager.has(name)) {
            sendMessage(COMMAND_SETWARP_FAILED_EXISTED.replace("<name>", name))
            return
        }
        if (!name.isValidIdentifier) {
            sendMessage(COMMAND_SETWARP_FAILED_NOT_VALID)
            return
        }
        if (name.length > WarpManager.nameLengthLimit) {
            sendMessage(COMMAND_SETWARP_FAILED_LENGTN_LIMIT)
            return
        }
        submitAsync {
            WarpManager.create(name, location, alias)
        }
        if (alias == null) {
            sendMessage(COMMAND_SETWARP_SUCCEED.replace("<name>", name))
        } else {
            sendMessage(
                COMMAND_SETWARP_SUCCEED_ALIAS
                    .replace("<name>", name)
                    .replace("<alias>", alias)
            )
        }
    }
}