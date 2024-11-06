package ink.pmc.essentials.commands.warp

import ink.pmc.advkt.component.text
import ink.pmc.advkt.send
import ink.pmc.essentials.api.warp.Warp
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.framework.utils.command.ensurePlayer
import ink.pmc.framework.utils.visual.mochaMaroon
import ink.pmc.framework.utils.visual.mochaPink
import ink.pmc.framework.utils.visual.mochaText
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotation.specifier.Quoted
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
object EditWarpCommand {
    @Command("editwarp <warp> alias <alias>")
    @Permission("essentials.editwarp")
    suspend fun CommandSender.alias(
        @Argument("warp", parserName = "warp-without-alias") warp: Warp,
        @Argument("alias") @Quoted alias: String
    ) {
        warp.alias = alias
        warp.update()
        send {
            text("已将地标 ") with mochaPink
            text("${warp.name} ") with mochaText
            text("的别名设置为 ") with mochaPink
            text(alias) with mochaText
        }
    }

    @Command("editwarp <warp> unset_alias")
    @Permission("essentials.editwarp")
    suspend fun CommandSender.unsetAlias(@Argument("warp", parserName = "warp-without-alias") warp: Warp) {
        warp.alias = null
        warp.update()
        send {
            text("已移除地标 ") with mochaPink
            text("${warp.name} ") with mochaText
            text("的别名") with mochaPink
        }
    }

    @Command("editwarp <warp> set_spawn")
    @Permission("essentials.editwarp")
    suspend fun CommandSender.spawn(@Argument("warp", parserName = "warp-without-alias") warp: Warp) {
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
    suspend fun CommandSender.unsetSpawn(@Argument("warp", parserName = "warp-without-alias") warp: Warp) {
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
    suspend fun CommandSender.setDefaultSpawn(@Argument("warp", parserName = "warp-without-alias") warp: Warp) {
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
    suspend fun CommandSender.unsetDefaultSpawn(@Argument("warp", parserName = "warp-without-alias") warp: Warp) {
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
    suspend fun CommandSender.move(@Argument("warp", parserName = "warp-without-alias") warp: Warp) = ensurePlayer {
        warp.location = location
        warp.update()
        send {
            text("已将地标 ") with mochaPink
            text("${warp.name} ") with mochaText
            text("迁移到你所在的位置") with mochaPink
        }
    }

    @Command("editwarp <warp> icon <material>")
    @Permission("essentials.editwarp")
    suspend fun CommandSender.icon(
        @Argument("warp", parserName = "warp-without-alias") warp: Warp,
        material: Material
    ) {
        warp.icon = material
        warp.update()
        send {
            text("已将地标 ") with mochaPink
            text("${warp.name} ") with mochaText
            text("的图标修改为 ") with mochaPink
            text("$material") with mochaText
        }
    }

    @Command("editwarp <warp> unset_icon")
    @Permission("essentials.editwarp")
    suspend fun CommandSender.unsetIcon(@Argument("warp", parserName = "warp-without-alias") warp: Warp) {
        warp.icon = null
        warp.update()
        send {
            text("已移除地标 ") with mochaPink
            text("${warp.name} ") with mochaText
            text("的图标") with mochaPink
        }
    }

    @Command("editwarp <warp> founder <founder>")
    @Permission("essentials.editwarp")
    suspend fun CommandSender.founder(
        @Argument("warp", parserName = "warp-without-alias") warp: Warp,
        founder: OfflinePlayer,
    ) {
        warp.founderId = founder.uniqueId
        warp.update()
        send {
            text("已将地标 ") with mochaPink
            text("${warp.name} ") with mochaText
            text("的设立人修改为 ") with mochaPink
            text(founder.name ?: founder.uniqueId.toString()) with mochaText
        }
    }

    @Command("editwarp <warp> unset_founder")
    @Permission("essentials.editwarp")
    suspend fun CommandSender.unsetFounder(@Argument("warp", parserName = "warp-without-alias") warp: Warp) {
        warp.founderId = null
        warp.update()
        send {
            text("已移除地标 ") with mochaPink
            text("${warp.name} ") with mochaText
            text("的设立人") with mochaPink
        }
    }

    @Command("editwarp <warp> description <description>")
    @Permission("essentials.editwarp")
    suspend fun CommandSender.description(
        @Argument("warp", parserName = "warp-without-alias") warp: Warp,
        @Argument("description", parserName = "editwarp-component") description: Component,
    ) {
        warp.description = description
        warp.update()
        send {
            text("已修改地标 ") with mochaPink
            text("${warp.name} ") with mochaText
            text("的简介") with mochaPink
        }
    }

    @Command("editwarp <warp> unset_description")
    @Permission("essentials.editwarp")
    suspend fun CommandSender.unsetDescription(@Argument("warp", parserName = "warp-without-alias") warp: Warp) {
        warp.description = null
        warp.update()
        send {
            text("已移除地标 ") with mochaPink
            text("${warp.name} ") with mochaText
            text("的简介") with mochaPink
        }
    }
}