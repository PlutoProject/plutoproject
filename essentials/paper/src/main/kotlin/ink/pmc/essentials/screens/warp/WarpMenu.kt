package ink.pmc.essentials.screens.warp

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.essentials.api.warp.Warp
import ink.pmc.framework.interactive.LocalPlayer
import ink.pmc.framework.interactive.inventory.Item
import ink.pmc.framework.interactive.inventory.Modifier
import ink.pmc.framework.interactive.inventory.components.Selector
import ink.pmc.framework.interactive.inventory.fillMaxSize
import ink.pmc.framework.interactive.inventory.jetpack.Arrangement
import ink.pmc.framework.interactive.inventory.layout.Menu
import ink.pmc.framework.interactive.inventory.layout.Row
import ink.pmc.framework.utils.time.zoneId
import ink.pmc.framework.utils.visual.mochaFlamingo
import ink.pmc.framework.utils.visual.mochaSubtext0
import ink.pmc.framework.utils.visual.mochaYellow
import net.kyori.adventure.text.Component
import org.bukkit.Material
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
                        Selector(
                            title = component {
                                text("筛选") with mochaYellow without italic()
                            },
                            options = listOf("全部", "已收藏", "仅看机械类", "仅看建筑类", "仅看城镇类"),
                            goNext = model::nextFilter,
                            goPrevious = model::previousFilter
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
        var founderName by remember {
            mutableStateOf(component {
                text("正在加载") with mochaSubtext0 without italic()
            })
        }
        if (warp.founder != null) {
            LaunchedEffect(Unit) {
                founderName = warp.founder?.let {
                    val player = it.await()
                    player.name?.let { name ->
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
            lore = listOf(
                component {
                    val time = ZonedDateTime.ofInstant(warp.createdAt, LocalPlayer.current.zoneId)
                    text("设立于 ") with mochaSubtext0 without italic()
                }
            )
        )
    }
}