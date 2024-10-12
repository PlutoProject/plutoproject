package ink.pmc.essentials.commands.warp

import ink.pmc.advkt.component.text
import ink.pmc.advkt.send
import ink.pmc.essentials.api.warp.Warp
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.essentials.commands.parseWarp
import ink.pmc.utils.BukkitCommandManager
import ink.pmc.utils.annotation.Command
import ink.pmc.utils.command.checkPlayer
import ink.pmc.utils.dsl.cloud.sender
import ink.pmc.utils.visual.mochaMaroon
import ink.pmc.utils.visual.mochaPink
import ink.pmc.utils.visual.mochaText
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.command.CommandSender
import org.incendo.cloud.context.CommandContext
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler
import org.incendo.cloud.parser.standard.StringParser
import org.koin.java.KoinJavaComponent.getKoin

@Command("editwarp")
@Suppress("UNUSED")
fun BukkitCommandManager.editWarp(aliases: Array<String>) {
    val base = commandBuilder("editwarp", *aliases)
        .permission("essentials.editwarp")
        .argument(warpsWithoutAlias("warp").required())

    command(
        base
            .literal("alias")
            .required("content", StringParser.quotedStringParser())
            .suspendingHandler {
                val sender = it.sender
                val warp = parseWarp(it.get("warp"))
                val content = it.get<String>("content")
                if (!it.checkWarp(warp)) return@suspendingHandler

                warp!!
                warp.alias = content
                warp.update()

                sender.send {
                    text("已将地标 ") with mochaPink
                    text("${warp.name} ") with mochaText
                    text("的别名设置为 ") with mochaPink
                    text(content) with mochaText
                }
            }
    )

    command(
        base
            .literal("set_spawn")
            .suspendingHandler {
                val sender = it.sender
                val manager = getKoin().get<WarpManager>()
                val warp = parseWarp(it.get("warp"))
                if (!it.checkWarp(warp)) return@suspendingHandler
                warp!!

                if (warp.isSpawn) {
                    sender.send {
                        text("该地标已是一个出生点") with mochaMaroon
                    }
                    return@suspendingHandler
                }

                manager.setSpawn(warp, true)
                sender.send {
                    text("已将地标 ") with mochaPink
                    text("${warp.name} ") with mochaText
                    text("设置为一个出生点") with mochaPink
                }
            }
    )

    command(
        base
            .literal("unset_spawn")
            .suspendingHandler {
                val sender = it.sender
                val manager = getKoin().get<WarpManager>()
                val warp = parseWarp(it.get("warp"))
                if (!it.checkWarp(warp)) return@suspendingHandler
                warp!!

                if (!warp.isSpawn) {
                    sender.send {
                        text("该地标不是一个出生点") with mochaMaroon
                    }
                    return@suspendingHandler
                }

                manager.setSpawn(warp, false)
                sender.send {
                    text("已将地标 ") with mochaPink
                    text("${warp.name} ") with mochaText
                    text("取消出生点") with mochaPink
                }
            }
    )

    command(
        base
            .literal("set_default_spawn")
            .suspendingHandler {
                val sender = it.sender
                val manager = getKoin().get<WarpManager>()
                val warp = parseWarp(it.get("warp"))
                if (!it.checkWarp(warp)) return@suspendingHandler
                warp!!

                if (warp.isDefaultSpawn) {
                    sender.send {
                        text("该地标已是默认出生点") with mochaMaroon
                    }
                    return@suspendingHandler
                }

                manager.setDefaultSpawn(warp, true)
                sender.send {
                    text("已将地标 ") with mochaPink
                    text("${warp.name} ") with mochaText
                    text("设置为默认出生点") with mochaPink
                }
            }
    )

    command(
        base
            .literal("unset_default_spawn")
            .suspendingHandler {
                val sender = it.sender
                val manager = getKoin().get<WarpManager>()
                val warp = parseWarp(it.get("warp"))
                if (!it.checkWarp(warp)) return@suspendingHandler
                warp!!

                if (!warp.isDefaultSpawn) {
                    sender.send {
                        text("该地标不是默认出生点") with mochaMaroon
                    }
                    return@suspendingHandler
                }

                manager.setDefaultSpawn(warp, false)
                sender.send {
                    text("已将地标 ") with mochaPink
                    text("${warp.name} ") with mochaText
                    text("取消默认出生点") with mochaPink
                }
            }
    )

    command(
        base
            .literal("move")
            .suspendingHandler {
                checkPlayer(it.sender) {
                    val warp = parseWarp(it.get("warp"))
                    if (!it.checkWarp(warp)) return@checkPlayer

                    warp!!
                    warp.location = location
                    warp.update()

                    send {
                        text("已将地标 ") with mochaPink
                        text("${warp.name} ") with mochaText
                        text("迁移到你所在的位置") with mochaPink
                    }
                }
            }
    )
}

private fun CommandContext<CommandSender>.checkWarp(warp: Warp?): Boolean {
    if (warp == null) {
        sender.send {
            text("该地标不存在") with mochaMaroon
        }
        return false
    }
    return true
}