package ink.pmc.essentials.commands.warp

import ink.pmc.advkt.component.text
import ink.pmc.advkt.send
import ink.pmc.essentials.api.warp.Warp
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.essentials.commands.parseWarp
import ink.pmc.framework.utils.command.ensurePlayer
import ink.pmc.framework.utils.visual.mochaMaroon
import ink.pmc.framework.utils.visual.mochaPink
import ink.pmc.framework.utils.visual.mochaText
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
object EditWarpCommand {
    @Command("editwarp <warp> alias <alias>")
    @Permission("essentials.editwarp")
    suspend fun CommandSender.editwarp(
        @Argument("warp", suggestions = "warps-without-alias") name: String,
        @Argument("alias") alias: String
    ) {
        val warp = parseAndCheck(name) ?: return
        warp.alias = alias
        warp.update()
        send {
            text("已将地标 ") with mochaPink
            text("${warp.name} ") with mochaText
            text("的别名设置为 ") with mochaPink
            text(alias) with mochaText
        }
    }

    @Command("editwarp <warp> set_spawn")
    @Permission("essentials.editwarp")
    suspend fun CommandSender.spawn(@Argument("warp", suggestions = "warps-without-alias") name: String) {
        val warp = parseAndCheck(name) ?: return
        if (warp.isSpawn) {
            send {
                text("该地标已是一个出生点") with mochaMaroon
            }
            return
        }
        WarpManager.setSpawn(warp, true)
        send {
            text("已将地标 ") with mochaPink
            text("${warp.name} ") with mochaText
            text("设置为一个出生点") with mochaPink
        }
    }

    @Command("editwarp <warp> unset_spawn")
    @Permission("essentials.editwarp")
    suspend fun CommandSender.unsetSpawn(@Argument("warp", suggestions = "warps-without-alias") name: String) {
        val warp = parseAndCheck(name) ?: return
        if (!warp.isSpawn) {
            send {
                text("该地标不是一个出生点") with mochaMaroon
            }
            return
        }
        WarpManager.setSpawn(warp, false)
        send {
            text("已将地标 ") with mochaPink
            text("${warp.name} ") with mochaText
            text("取消出生点") with mochaPink
        }
    }

    @Command("editwarp <warp> set_default_spawn")
    @Permission("essentials.editwarp")
    suspend fun CommandSender.setDefaultSpawn(@Argument("warp", suggestions = "warps-without-alias") name: String) {
        val warp = parseAndCheck(name) ?: return
        if (warp.isDefaultSpawn) {
            send {
                text("该地标已是默认出生点") with mochaMaroon
            }
            return
        }
        WarpManager.setDefaultSpawn(warp, true)
        send {
            text("已将地标 ") with mochaPink
            text("${warp.name} ") with mochaText
            text("设置为默认出生点") with mochaPink
        }
    }

    @Command("editwarp <warp> unset_default_spawn")
    @Permission("essentials.editwarp")
    suspend fun CommandSender.unsetDefaultSpawn(@Argument("warp", suggestions = "warps-without-alias") name: String) {
        val warp = parseAndCheck(name) ?: return
        if (!warp.isDefaultSpawn) {
            send {
                text("该地标不是默认出生点") with mochaMaroon
            }
            return
        }
        WarpManager.setDefaultSpawn(warp, false)
        send {
            text("已将地标 ") with mochaPink
            text("${warp.name} ") with mochaText
            text("取消默认出生点") with mochaPink
        }
    }

    @Command("editwarp <warp> move")
    @Permission("essentials.editwarp")
    suspend fun CommandSender.move(@Argument("warp", suggestions = "warps-without-alias") name: String) = ensurePlayer {
        val warp = parseAndCheck(name) ?: return
        warp.location = location
        warp.update()
        send {
            text("已将地标 ") with mochaPink
            text("${warp.name} ") with mochaText
            text("迁移到你所在的位置") with mochaPink
        }
    }
}