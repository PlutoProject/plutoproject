package ink.pmc.essentials.screens.warp

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.raw
import ink.pmc.advkt.component.text
import ink.pmc.essentials.api.warp.Warp
import ink.pmc.essentials.api.warp.WarpCategory.*
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.framework.interactive.LocalPlayer
import ink.pmc.framework.interactive.inventory.Item
import ink.pmc.framework.interactive.inventory.Modifier
import ink.pmc.framework.interactive.inventory.click.clickable
import ink.pmc.framework.interactive.inventory.components.Selector
import ink.pmc.framework.interactive.inventory.components.SeparatePageTuner
import ink.pmc.framework.interactive.inventory.components.SeparatePageTunerMode
import ink.pmc.framework.interactive.inventory.fillMaxSize
import ink.pmc.framework.interactive.inventory.jetpack.Arrangement
import ink.pmc.framework.interactive.inventory.layout.Menu
import ink.pmc.framework.interactive.inventory.layout.Row
import ink.pmc.framework.utils.chat.splitLines
import ink.pmc.framework.utils.time.formatDate
import ink.pmc.framework.utils.time.zoneId
import ink.pmc.framework.utils.visual.*
import ink.pmc.framework.utils.world.aliasOrName
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import java.time.ZonedDateTime

class WarpMenu : Screen {
    override val key: ScreenKey = "essentials_warp_menu"
    private val model: ProvidableCompositionLocal<WarpMenuModel> = staticCompositionLocalOf { error("Uninitialized") }

    @Composable
    override fun Content() {
        val player = LocalPlayer.current
        val model = rememberScreenModel { WarpMenuModel(player) }

        LaunchedEffect(model.page) {
            model.loadPage()
        }

        CompositionLocalProvider(
            this.model provides model
        ) {
            Menu(
                title = Component.text("地标"),
                rows = 6,
                leftBorder = false,
                rightBorder = false,
                bottomBorderAttachment = {
                    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
                        SeparatePageTuner(
                            mode = SeparatePageTunerMode.PREVIOUS,
                            current = model.page + 1,
                            total = model.pageCount,
                            turn = { if (model.page > 0) model.page-- }
                        )
                        Selector(
                            title = component {
                                text("筛选") with mochaYellow without italic()
                            },
                            options = listOf("全部", "已收藏", "仅看机械类", "仅看建筑类", "仅看城镇类"),
                            goNext = model::nextFilter,
                            goPrevious = model::previousFilter
                        )
                        SeparatePageTuner(
                            mode = SeparatePageTunerMode.NEXT,
                            current = model.page + 1,
                            total = model.pageCount,
                            turn = { if (model.page < model.pageCount - 1) model.page++ }
                        )
                    }
                }
            ) {
                val contents = this.model.current.contents
                contents.forEach {
                    Warp(it)
                }
            }
        }
    }

    @Suppress("FunctionName")
    @Composable
    fun Warp(warp: Warp) {
        val player = LocalPlayer.current
        var founderName by remember {
            mutableStateOf(component {
                text("正在加载") with mochaSubtext0 without italic()
            })
        }
        var isInCollection by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            isInCollection = WarpManager.getCollection(player).contains(warp)
        }
        if (warp.founder != null) {
            LaunchedEffect(Unit) {
                founderName = warp.founder?.let {
                    val founder = it.await()
                    founder.name?.let { name ->
                        component {
                            text(name) with mochaFlamingo without italic()
                        }
                    }
                } ?: component {
                    text("未知名称") with mochaSubtext0 without italic()
                }
            }
        }
        Item(
            material = warp.icon ?: Material.PAPER,
            name = component {
                if (warp.alias != null) {
                    text("${warp.alias} ") with mochaYellow without italic()
                    text("(${warp.name})") with mochaSubtext0 without italic()
                } else {
                    text(warp.name) with mochaYellow without italic()
                }
            },
            enchantmentGlint = isInCollection,
            lore = buildList {
                if (isInCollection) {
                    add(component {
                        text("✨ 已收藏") with mochaYellow
                    })
                }
                when (warp.category) {
                    MACHINE -> component {
                        text("\uD83D\uDD27 机械类") with mochaTeal without italic()
                    }

                    ARCHITECTURE -> component {
                        text("\uD83D\uDDFC 建筑类") with mochaFlamingo without italic()
                    }

                    TOWN -> component {
                        text("\uD83D\uDE84 城镇类") with mochaMauve without italic()
                    }

                    null -> {}
                }
                add(component {
                    if (warp.founder != null) {
                        text("由 ") with mochaSubtext0 without italic()
                        raw(founderName)
                        text(" ")
                    }
                    val time = ZonedDateTime.ofInstant(warp.createdAt, player.zoneId).formatDate()
                    text("设立于 ") with mochaSubtext0 without italic()
                    text(time) with mochaText without italic()
                })
                add(component {
                    val world = warp.location.world.aliasOrName
                    val x = warp.location.blockX
                    val y = warp.location.blockY
                    val z = warp.location.blockZ
                    text("$world $x, $y, $z")
                })
                add(Component.empty())
                warp.description?.let {
                    addAll(it.splitLines().map { line ->
                        line.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    })
                }
                add(Component.empty())
                add(component {
                    text("左键 ") with mochaLavender without italic()
                    text("前往此处") with mochaText without italic()
                })
                add(component {
                    text("Shift + 左键 ") with mochaLavender without italic()
                    text("收藏") with mochaText without italic()
                })
            },
            modifier = Modifier.clickable {
                when (clickType) {
                    ClickType.LEFT -> {
                        player.closeInventory()
                        warp.teleport(player)
                    }

                    ClickType.SHIFT_LEFT -> {
                        if (WarpManager.getCollection(player).contains(warp)) {
                            WarpManager.removeFromCollection(player, warp)
                        } else {
                            WarpManager.addToCollection(player, warp)
                        }
                        isInCollection = !isInCollection
                    }

                    else -> {}
                }
            }
        )
    }
}