package ink.pmc.daily.commands

import ink.pmc.advkt.component.text
import ink.pmc.advkt.send
import ink.pmc.daily.PERMISSION_CMD_DAILY
import ink.pmc.daily.plugin
import ink.pmc.utils.BukkitCommandManager
import ink.pmc.utils.annotation.Command
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import ink.pmc.utils.visual.mochaPink

@Command("daily")
@Suppress("UNUSED")
fun BukkitCommandManager.daily(aliases: Array<String>) {
    this("daily", *aliases) {
        "reload" {
            permission(PERMISSION_CMD_DAILY)
            handler {
                plugin.reload()
                sender.send {
                    text("已重载配置文件") with mochaPink
                }
            }
        }
    }
}