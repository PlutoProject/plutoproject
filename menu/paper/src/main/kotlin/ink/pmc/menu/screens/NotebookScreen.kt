package ink.pmc.menu.screens

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.keybind
import ink.pmc.advkt.component.text
import ink.pmc.advkt.send
import ink.pmc.daily.screens.DailyCalenderScreen
import ink.pmc.essentials.DEFAULT_ECONOMY_SYMBOL
import ink.pmc.essentials.RANDOM_TELEPORT_COST_BYPASS
import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.essentials.api.teleport.random.RandomTeleportManager
import ink.pmc.essentials.screens.home.HomeListScreen
import ink.pmc.essentials.screens.teleport.TeleportRequestScreen
import ink.pmc.essentials.screens.warp.DefaultSpawnPickerScreen
import ink.pmc.essentials.screens.warp.WarpListScreen
import ink.pmc.framework.interactive.InteractiveScreen
import ink.pmc.framework.interactive.LocalPlayer
import ink.pmc.framework.interactive.inventory.*
import ink.pmc.framework.interactive.inventory.canvas.Chest
import ink.pmc.framework.interactive.inventory.click.clickable
import ink.pmc.framework.interactive.inventory.jetpack.Arrangement
import ink.pmc.framework.interactive.inventory.layout.Box
import ink.pmc.framework.interactive.inventory.layout.Column
import ink.pmc.framework.interactive.inventory.layout.Row
import ink.pmc.framework.playerdb.PlayerDb
import ink.pmc.framework.utils.chat.UI_PAGING_SOUND
import ink.pmc.framework.utils.chat.UI_SUCCEED_SOUND
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.concurrent.sync
import ink.pmc.framework.utils.hook.economyHook
import ink.pmc.framework.utils.time.formatDuration
import ink.pmc.framework.utils.trimmed
import ink.pmc.framework.utils.visual.*
import ink.pmc.framework.utils.world.aliasOrName
import ink.pmc.hypervisor.DynamicScheduling
import ink.pmc.hypervisor.DynamicViewDistanceState.*
import ink.pmc.menu.CO_NEAR_COMMAND
import ink.pmc.menu.components.Wiki
import ink.pmc.menu.economy
import ink.pmc.menu.inspecting
import ink.pmc.menu.screens.MainMenuModel.PreferredHomeState
import ink.pmc.menu.screens.MainMenuModel.PreferredSpawnState
import ink.pmc.menu.screens.MainMenuModel.Tab.ASSIST
import ink.pmc.menu.screens.MainMenuModel.Tab.HOME
import ink.pmc.menu.screens.NotebookScreen.RandomTeleportState.*
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.koin.core.component.KoinComponent
import kotlin.time.Duration.Companion.ZERO

private const val PANE_COLUMNS = 4
private const val PANE_COLUMN_WIDTH = 7
private const val PANE_GRIDS = PANE_COLUMNS * PANE_COLUMN_WIDTH
private const val FIRST_OPEN_PROMPT_KEY = "menu.notebook.was_opened_before"

private val LOADING_LORE = listOf(
    component {
        text("正在加载...") with mochaSubtext0 without italic()
    }
)

class NotebookScreen : InteractiveScreen(), KoinComponent {
    private val localScreenModel: ProvidableCompositionLocal<MainMenuModel> =
        staticCompositionLocalOf { error("Unexpected") }

    @Composable
    override fun Content() {
        val player = LocalPlayer.current
        val model = rememberScreenModel { MainMenuModel(player) }

        // 初次打开或翻到其他页面再回来时触发，重载可能被修改的数据
        LaunchedEffect(Unit) {
            model.loadInformation()
        }

        LaunchedEffect(Unit) {
            val db = PlayerDb.getOrCreate(player.uniqueId)
            if (db.getBoolean(FIRST_OPEN_PROMPT_KEY)) return@LaunchedEffect
            player.send {
                text("小提示: 你可以使用 ") with mochaText
                keybind("key.sneak") with mochaLavender
                text(" + ") with mochaLavender
                keybind("key.swapOffhand") with mochaLavender
                text(" 或 ") with mochaText
                text("/menu ") with mochaLavender
                text("来打开「手账」") with mochaText
            }
            db[FIRST_OPEN_PROMPT_KEY] = true
            db.update()
        }

        CompositionLocalProvider(localScreenModel provides model) {
            Chest(
                title = component {
                    text("手账")
                },
                modifier = Modifier.height(6)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Background()
                    Column(modifier = Modifier.fillMaxSize()) {
                        InnerContents()
                    }
                }
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun InnerContents() {
        TopBar()
        Pane()
    }

    @Composable
    @Suppress("FunctionName")
    private fun TopBar() {
        Row(modifier = Modifier.height(1).fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            TabSwitcher(
                icon = Material.CAMPFIRE,
                name = component {
                    text("主页") with mochaText without italic()
                },
                lore = emptyList(),
                tab = HOME
            )
            Spacer(modifier = Modifier.width(1).height(1))
            TabSwitcher(
                icon = Material.TRIPWIRE_HOOK,
                name = component {
                    text("辅助功能") with mochaText without italic()
                },
                lore = emptyList(),
                tab = ASSIST
            )
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun TabSwitcher(
        icon: Material,
        name: Component,
        lore: List<Component>,
        tab: MainMenuModel.Tab
    ) {
        val model = localScreenModel.current
        val player = LocalPlayer.current
        Item(
            material = icon,
            name = name,
            lore = lore,
            modifier = Modifier.clickable {
                if (clickType != ClickType.LEFT) return@clickable
                if (model.tab == tab) return@clickable
                model.tab = tab
                player.playSound(UI_PAGING_SOUND)
            }
        )
    }

    @Composable
    @Suppress("FunctionName")
    private fun Pane() {
        val model = localScreenModel.current
        Row(
            modifier = Modifier.height(PANE_COLUMNS).fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.fillMaxHeight().width(PANE_COLUMN_WIDTH)) {
                VerticalGrid(modifier = Modifier.fillMaxSize()) {
                    repeat(PANE_GRIDS) {
                        ItemEmpty()
                    }
                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                ) {
                    when (model.tab) {
                        HOME -> HomeTab()
                        ASSIST -> AssistTab()
                    }
                }
            }
        }
    }

    // 主页面
    @Composable
    @Suppress("FunctionName")
    private fun HomeTab() {
        Row(modifier = Modifier.fillMaxWidth().height(1), horizontalArrangement = Arrangement.Center) {
            Home()
            Spawn()
            Warp()
            Teleport()
            RandomTeleport()
        }
        Row(modifier = Modifier.fillMaxWidth().height(1), horizontalArrangement = Arrangement.Center) {
            Daily()
            Coin()
            Wiki()
        }
    }

    // 辅助功能页面
    @Composable
    @Suppress("FunctionName")
    private fun AssistTab() {
        Row(modifier = Modifier.fillMaxWidth().height(1), horizontalArrangement = Arrangement.Center) {
            Lookup()
            ItemEmpty()
            ViewBoost()
        }
        Row(modifier = Modifier.fillMaxWidth().height(1), horizontalArrangement = Arrangement.Center) {
            Empty(modifier = Modifier.fillMaxSize())
        }
    }

    private val homeDesc = component {
        text("为你指明归家路的一盏灯") with mochaSubtext0 without italic()
    }


    private val homeOpenList = component {
        text("右键 ") with mochaLavender without italic()
        text("打开家列表") with mochaText without italic()
    }

    /*
    * 家
    */
    @Composable
    @Suppress("FunctionName")
    private fun Home() {
        val player = LocalPlayer.current
        val model = localScreenModel.current
        val navigator = LocalNavigator.currentOrThrow

        Item(
            material = Material.LANTERN,
            name = component {
                text("明灯") with mochaYellow without italic()
            },
            lore = when (model.preferredHomeState) {
                is PreferredHomeState.Loading -> LOADING_LORE
                is PreferredHomeState.Ready -> buildList {
                    add(homeDesc)
                    add(Component.empty())
                    add(component {
                        text("左键 ") with mochaLavender without italic()
                        text("传送至首选的家") with mochaText without italic()
                    })
                    add(homeOpenList)
                }

                is PreferredHomeState.None -> buildList {
                    add(homeDesc)
                    add(Component.empty())
                    add(component {
                        text("你还没有首选的家") with mochaSubtext0 without italic()
                    })
                    add(component {
                        text("请在编辑家页面中点击「设为首选」") with mochaSubtext0 without italic()
                    })
                    add(Component.empty())
                    add(homeOpenList)
                }
            },
            // enchantmentGlint = state.value > 0,
            modifier = Modifier.clickable {
                when (clickType) {
                    ClickType.LEFT -> {
                        val preferred = (model.preferredHomeState as? PreferredHomeState.Ready)?.home
                        preferred?.teleport(player)
                    }

                    ClickType.RIGHT -> {
                        navigator.push(HomeListScreen(player))
                    }

                    else -> {}
                }
            }
        )
    }

    /*
    * 家
    */
    @Composable
    @Suppress("FunctionName")
    private fun Spawn() {
        val navigator = LocalNavigator.currentOrThrow
        val player = LocalPlayer.current
        val model = localScreenModel.current

        Item(
            material = Material.COMPASS,
            name = component {
                text("伊始之处") with mochaFlamingo without italic()
            },
            lore = when (model.preferredSpawnState) {
                is PreferredSpawnState.Loading -> LOADING_LORE
                is PreferredSpawnState.Ready -> {
                    val spawn = (model.preferredSpawnState as PreferredSpawnState.Ready).spawn
                    val lore = buildList {
                        add(component {
                            text("旅途的起点") with mochaSubtext0 without italic()
                        })
                        add(Component.empty())
                        add(component {
                            text("左键 ") with mochaLavender without italic()
                            text("回到主城") with mochaText without italic()
                        })
                        add(component {
                            text("右键 ") with mochaLavender without italic()
                            text("设置首选主城") with mochaText without italic()
                        })
                    }
                    val name = when (spawn.alias) {
                        null -> component { text(spawn.name) with mochaText without italic() }
                        else -> component { text(spawn.alias!!) with mochaText without italic() }
                    }
                    val loc = spawn.let {
                        val world = it.location.world.aliasOrName
                        val x = it.location.blockX
                        val y = it.location.blockY
                        val z = it.location.blockZ
                        component { text("$world $x, $y, $z") with mochaSubtext0 without italic() }
                    }
                    lore.replace("<spawn>", name).replace("<loc>", loc)
                }

                is PreferredSpawnState.None -> buildList {
                    add(component {
                        text("你还没有首选的主城") with mochaSubtext0 without italic()
                    })
                    add(component {
                        text("右键点击来设置") with mochaSubtext0 without italic()
                    })
                }
            }.toList(),
            modifier = Modifier.clickable {
                when (clickType) {
                    ClickType.LEFT -> {
                        val spawn = (model.preferredSpawnState as? PreferredSpawnState.Ready)?.spawn ?: return@clickable
                        spawn.teleport(player)
                    }

                    ClickType.RIGHT -> navigator.push(DefaultSpawnPickerScreen())
                    else -> {}
                }
            }
        )
    }

    /*
    * 玩家间传送
    */
    @Composable
    @Suppress("FunctionName")
    private fun Teleport() {
        val player = LocalPlayer.current
        val navigator = LocalNavigator.currentOrThrow
        val hasUnfinishedTpRequest = TeleportManager.hasUnfinishedRequest(player)
        Item(
            material = Material.ENDER_PEARL,
            name = component {
                text("定向传送") with mochaGreen without italic()
            },
            lore = if (!hasUnfinishedTpRequest) buildList {
                add(component {
                    text("拜访世界中的其他玩家") with mochaSubtext0 without italic()
                })
                add(Component.empty())
                add(component {
                    text("左键 ") with mochaLavender without italic()
                    text("发起传送请求") with mochaText without italic()
                })
            } else buildList {
                add(component {
                    text("你还有未完成的传送请求") with mochaSubtext0 without italic()
                })
                add(component {
                    text("可使用 ") with mochaSubtext0 without italic()
                    text("/tpcancel ") with mochaLavender without italic()
                    text("来取消") with mochaSubtext0 without italic()
                })
            },
            enchantmentGlint = hasUnfinishedTpRequest,
            modifier = Modifier.clickable {
                if (hasUnfinishedTpRequest) return@clickable
                if (clickType != ClickType.LEFT) return@clickable
                navigator.push(TeleportRequestScreen())
            }
        )
    }

    // 可用，货币不足，该世界不可用，冷却中
    private enum class RandomTeleportState {
        AVAILABLE, COIN_NOT_ENOUGH, NOT_AVAILABLE, IN_COOLDOWN
    }

    /*
    * 随机传送
    */
    @Composable
    @Suppress("FunctionName")
    private fun Warp() {
        val navigator = LocalNavigator.currentOrThrow
        Item(
            material = Material.MINECART,
            name = component {
                text("巡回列车") with mochaSapphire without italic()
            },
            lore = buildList {
                add(component {
                    text("参观其他玩家的机械、建筑与城镇") with mochaSubtext0 without italic()
                })
                add(Component.empty())
                add(component {
                    text("左键 ") with mochaLavender without italic()
                    text("打开地标列表") with mochaText without italic()
                })
            },
            modifier = Modifier.clickable {
                if (clickType != ClickType.LEFT) return@clickable
                navigator.push(WarpListScreen())
            }
        )
    }

    private val economySymbol = economyHook?.provider?.currencyNameSingular() ?: DEFAULT_ECONOMY_SYMBOL

    /*
    * 随机传送
    */
    @Composable
    @Suppress("FunctionName")
    private fun RandomTeleport() {
        val model = localScreenModel.current
        val player = LocalPlayer.current
        val world = player.world
        val balance = economy.getBalance(player)
        val requirement = RandomTeleportManager.getRandomTeleportOptions(world).cost

        val teleportCost = RandomTeleportManager.getRandomTeleportOptions(player.world).cost.trimmed()
        val teleportCostMessage = "$teleportCost$economySymbol"

        LaunchedEffect(Unit) {
            while (true) {
                delay(500)
                model.refreshCooldownState()
            }
        }

        val state = when {
            model.rtpCooldownRemaining > ZERO -> IN_COOLDOWN
            !player.hasPermission(RANDOM_TELEPORT_COST_BYPASS) && balance < requirement -> COIN_NOT_ENOUGH
            !RandomTeleportManager.isEnabled(world) -> NOT_AVAILABLE
            else -> AVAILABLE
        }

        Item(
            material = Material.AMETHYST_SHARD,
            name = component {
                text("神奇水晶") with mochaMauve without italic()
            },
            lore = when (state) {
                AVAILABLE -> buildList {
                    add(component {
                        text("具有魔力的紫水晶") with mochaSubtext0 without italic()
                    })
                    add(component {
                        text("可以带你去世界上的另一个角落") with mochaSubtext0 without italic()
                    })
                    add(Component.empty())
                    add(component {
                        text("左键 ") with mochaLavender without italic()
                        text("进行随机传送 ") with mochaText without italic()
                        text("($teleportCostMessage)") with mochaSubtext0 without italic()
                    })
                }

                NOT_AVAILABLE -> buildList {
                    add(component {
                        text("该世界未启用随机传送") with mochaSubtext0 without italic()
                    })
                }

                COIN_NOT_ENOUGH -> buildList {
                    add(component {
                        text("货币不足") with mochaSubtext0 without italic()
                    })
                    add(component {
                        text("进行随机传送需要 ") with mochaSubtext0 without italic()
                        text(teleportCostMessage) with mochaText without italic()
                    })
                }

                IN_COOLDOWN -> buildList {
                    add(component {
                        text("传送冷却中...") with mochaSubtext0 without italic()
                    })
                    add(component {
                        text("还剩 ") with mochaSubtext0 without italic()
                        text(model.rtpCooldownRemaining.formatDuration()) with mochaText without italic()
                    })
                }
            },
            modifier = Modifier.clickable {
                if (state != AVAILABLE) return@clickable
                if (clickType != ClickType.LEFT) return@clickable
                RandomTeleportManager.launch(player, player.world)
                sync {
                    player.closeInventory()
                }
            }
        )
    }

    /*
    * 查询周围
    */
    @Composable
    @Suppress("FunctionName")
    private fun Lookup() {
        val player = LocalPlayer.current
        val model = localScreenModel.current
        Item(
            material = Material.ENDER_EYE,
            name = if (!model.lookupModeEnabled) component {
                text("观察模式 ") with mochaText without italic()
                text("关") with mochaMaroon without italic()
            } else component {
                text("观察模式 ") with mochaText without italic()
                text("开") with mochaGreen without italic()
            },
            lore = if (!model.lookupModeEnabled) buildList {
                add(component {
                    text("将周围的变化一览无余") with mochaSubtext0 without italic()
                })
                add(Component.empty())
                add(component {
                    text("左键 ") with mochaLavender without italic()
                    text("开启观察模式") with mochaText without italic()
                })
                add(component {
                    text("右键 ") with mochaLavender without italic()
                    text("观察四周变化") with mochaText without italic()
                })
            } else buildList {
                add(component {
                    text("将周围的变化一览无余") with mochaSubtext0 without italic()
                })
                add(Component.empty())
                add(component {
                    text("观察模式已开启") with mochaSubtext0 without italic()
                })
                add(component {
                    text("使用左键或右键点击来观察变化") with mochaSubtext0 without italic()
                })
                add(Component.empty())
                add(component {
                    text("左键 ") with mochaLavender without italic()
                    text("关闭观察模式") with mochaText without italic()
                })
            },
            enchantmentGlint = model.lookupModeEnabled,
            modifier = Modifier.clickable {
                if (!model.lookupModeEnabled) {
                    when (clickType) {
                        ClickType.LEFT -> {
                            player.inspecting = true
                            model.lookupModeEnabled = true
                            player.playSound(UI_SUCCEED_SOUND)
                            return@clickable
                        }

                        ClickType.RIGHT -> {
                            player.playSound(UI_SUCCEED_SOUND)
                            sync {
                                player.performCommand(CO_NEAR_COMMAND)
                                player.closeInventory()
                            }
                        }

                        else -> {}
                    }
                    return@clickable
                }

                if (model.lookupModeEnabled && clickType == ClickType.LEFT && player.inspecting) {
                    player.inspecting = false
                    model.lookupModeEnabled = false
                    player.playSound(UI_SUCCEED_SOUND)
                    return@clickable
                }
            }
        )
    }

    private val dailyOperation = component {
        text("左键 ") with mochaLavender without italic()
        text("打开礼记日历") with mochaText without italic()
    }

    private val dailyIntroduction = component {
        text("时光与点滴足迹") with mochaSubtext0 without italic()
    }

    /*
    * 每日签到
    */
    @Composable
    @Suppress("FunctionName")
    private fun Daily() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = localScreenModel.current
        Item(
            material = Material.NAME_TAG,
            name = component {
                text("礼记") with mochaPink without italic()
            },
            lore = if (!screenModel.isCheckedInToday) buildList {
                add(component {
                    text("× 今日尚未到访") with mochaYellow without italic()
                })
                add(dailyIntroduction)
                add(Component.empty())
                add(dailyOperation)
            } else buildList {
                add(component {
                    text("√ 今日已到访") with mochaGreen without italic()
                })
                add(dailyIntroduction)
                add(Component.empty())
                add(dailyOperation)
            },
            modifier = Modifier.clickable {
                if (clickType != ClickType.LEFT) return@clickable
                navigator.push(DailyCalenderScreen())
            }
        )
    }

    /*
    * 货币信息
    */
    @Composable
    @Suppress("FunctionName")
    private fun Coin() {
        val player = LocalPlayer.current
        Item(
            material = Material.SUNFLOWER,
            name = component {
                text("货币") with mochaYellow without italic()
            },
            lore = buildList {
                add(component {
                    val balance = economy.getBalance(player).trimmed()
                    text("你的余额: ") with mochaSubtext0 without italic()
                    text("$balance$DEFAULT_ECONOMY_SYMBOL") with mochaText without italic()
                })
                add(component {
                    text("可在「礼记」中到访来获取货币") with mochaSubtext0 without italic()
                })
            }
        )
    }

    private val disabled = component {
        text("关") with mochaMaroon without italic()
    }

    private val enabled = component {
        text("开") with mochaGreen without italic()
    }

    private val viewBoost = component {
        text("视距拓展") with mochaText without italic()
    }

    private val viewBoostDesc = listOf(
        component {
            text("可让服务器为你发送至多 ") with mochaSubtext0 without italic()
            text("16 ") with mochaText without italic()
            text("视距") with mochaSubtext0 without italic()
        },
        component {
            text("以提升观景体验") with mochaSubtext0 without italic()
        }
    )

    private val viewBoostDisabledDuePing = buildList {
        addAll(viewBoostDesc)
        add(Component.empty())
        add(component {
            text("此功能仅在延迟小于 ") with mochaYellow without italic()
            text("100ms ") with mochaText without italic()
            text("时可用") with mochaYellow without italic()
        })
        add(component {
            text("可尝试切换到一个质量更好的网络接入点") with mochaYellow without italic()
        })
    }

    /*
    * 视距拓展
    * */
    @Composable
    @Suppress("FunctionName")
    private fun ViewBoost() {
        val player = LocalPlayer.current
        var state by mutableStateOf(DynamicScheduling.getViewDistanceLocally(player))
        Item(
            material = Material.SPYGLASS,
            name = when (state) {
                ENABLED -> viewBoost
                    .append(Component.text(" "))
                    .append(enabled)

                DISABLED -> viewBoost
                    .append(Component.text(" "))
                    .append(disabled)

                DISABLED_DUE_PING -> viewBoost
                    .append(Component.text(" "))
                    .append(disabled)

                ENABLED_BUT_DISABLED_DUE_PING -> viewBoost
                    .append(Component.text(" "))
                    .append(disabled)

                DISABLED_DUE_VHOST -> viewBoost
                    .append(Component.text(" "))
                    .append(disabled)
            },
            enchantmentGlint = state == ENABLED,
            lore = when (state) {
                ENABLED -> buildList {
                    addAll(viewBoostDesc)
                    add(Component.empty())
                    add(component {
                        text("将渲染距离调至 ") with mochaSubtext0 without italic()
                        text("16 ") with mochaText without italic()
                        text("或更高") with mochaSubtext0 without italic()
                    })
                    add(component {
                        text("以使此功能生效") with mochaSubtext0 without italic()
                    })
                    add(Component.empty())
                    add(component {
                        text("左键 ") with mochaLavender without italic()
                        text("关闭功能") with mochaText without italic()
                    })
                }

                DISABLED -> buildList {
                    addAll(viewBoostDesc)
                    add(Component.empty())
                    add(component {
                        text("左键 ") with mochaLavender without italic()
                        text("开启功能") with mochaText without italic()
                    })
                }

                DISABLED_DUE_PING -> viewBoostDisabledDuePing
                ENABLED_BUT_DISABLED_DUE_PING -> viewBoostDisabledDuePing
                DISABLED_DUE_VHOST -> buildList {
                    addAll(viewBoostDesc)
                    add(Component.empty())
                    add(component {
                        text("你正在使用的连接线路不支持此功能") with mochaYellow without italic()
                    })
                    add(component {
                        text("请切换至主线路") with mochaYellow without italic()
                    })
                }
            },
            modifier = Modifier.clickable {
                if (clickType != ClickType.LEFT) return@clickable
                when (state) {
                    ENABLED -> {
                        DynamicScheduling.setViewDistance(player, false)
                        player.playSound(UI_SUCCEED_SOUND)
                        state = DISABLED
                    }

                    DISABLED -> {
                        DynamicScheduling.setViewDistance(player, true)
                        player.playSound(UI_SUCCEED_SOUND)
                        state = ENABLED
                    }

                    DISABLED_DUE_PING -> return@clickable
                    DISABLED_DUE_VHOST -> return@clickable
                    ENABLED_BUT_DISABLED_DUE_PING -> return@clickable
                }
            }
        )
    }
}