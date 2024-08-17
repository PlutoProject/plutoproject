package ink.pmc.daily.commands

import ink.pmc.daily.COMMAND_DAILY_RELOAD
import ink.pmc.daily.PERMISSION_CMD_DAILY
import ink.pmc.daily.plugin
import ink.pmc.utils.PaperCm
import ink.pmc.utils.annotation.Command
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender

@Command("daily")
@Suppress("UNUSED")
fun PaperCm.daily(aliases: Array<String>) {
    this("daily", *aliases) {
        "reload" {
            permission(PERMISSION_CMD_DAILY)
            handler {
                plugin.reload()
                sender.sender.sendMessage(COMMAND_DAILY_RELOAD)
            }
        }
    }
}