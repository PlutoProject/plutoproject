package ink.pmc.serverselector.screen

import androidx.compose.runtime.*
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.framework.bridge.Bridge
import ink.pmc.framework.interactive.InteractiveScreen
import ink.pmc.framework.interactive.LocalPlayer
import ink.pmc.framework.interactive.inventory.*
import ink.pmc.framework.interactive.inventory.click.clickable
import ink.pmc.framework.interactive.inventory.jetpack.Arrangement
import ink.pmc.framework.interactive.inventory.layout.Column
import ink.pmc.framework.interactive.inventory.layout.Menu
import ink.pmc.framework.interactive.inventory.layout.Row
import ink.pmc.framework.options.OptionsManager
import ink.pmc.framework.utils.chat.UI_SUCCEED_SOUND
import ink.pmc.framework.utils.concurrent.submitAsync
import ink.pmc.framework.utils.concurrent.sync
import ink.pmc.framework.utils.visual.*
import ink.pmc.serverselector.*
import ink.pmc.serverselector.screen.ServerSelectorScreen.AutoJoinState.*
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Duration.Companion.seconds

class ServerSelectorScreen : InteractiveScreen(), KoinComponent {
    private val config by inject<ServerSelectorConfig>()

    @Composable
    override fun Content() {
        Menu(
            title = Component.text("选择服务器"),
            rows = config.menu.rows,
            bottomBorderAttachment = {
                Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
                    AutoJoin()
                }
            }
        ) {
            println("---------- Menu Start ----------")
            Column(modifier = Modifier.fillMaxSize()) {
                config.menu.pattern.forEach {
                    println("Pattern forEach: $it")
                    PatternLine(it)
                }
            }
            println("---------- Menu End ----------")
        }
    }

    @Suppress("FunctionName")
    @Composable
    private fun PatternLine(pattern: String) {
        println("---------- Pattern Line start: $pattern ----------")
        Row(modifier = Modifier.fillMaxWidth().height(1)) {
            pattern.forEach { char ->
                println("Pattern Line forEach: $char")
                if (char.isWhitespace()) {
                    println("Char is whitespace (char = $char), put ItemSpacer")
                    ItemSpacer()
                    return@forEach
                }
                val server = config.servers.firstOrNull { it.menuIcon.first() == char }
                val ingredient = config.menu.ingredients[char.toString()]
                if (ingredient == null || server == null) {
                    println("Ingredient or server is null (ingredient = $ingredient, server = $server), put ItemSpacer")
                    ItemSpacer()
                    return@forEach
                }
                Server(server, ingredient)
            }
        }
        println("---------- Pattern line end ----------")
    }

    @Suppress("FunctionName")
    @Composable
    private fun Server(server: Server, ingredient: Ingredient) {
        println("---------- Server start: ${server.name}, ${server.menuIcon}, ${server.bridgeId} ----------")
        val player = LocalPlayer.current
        var bridgeServer by remember(server) { mutableStateOf(Bridge.getServer(server.bridgeId)) }
        var isOnline by remember(server) { mutableStateOf(bridgeServer?.isOnline ?: false) }
        var playerCount by remember(server) { mutableStateOf(bridgeServer?.playerCount ?: 0) }
        LaunchedEffect(server) {
            while (true) {
                delay(1.seconds)
                val current = Bridge.getServer(server.bridgeId)
                if (bridgeServer != current) {
                    bridgeServer = current
                }
                isOnline = bridgeServer?.isOnline ?: false
                playerCount = if (isOnline) bridgeServer!!.playerCount else 0
            }
        }
        Item(
            material = ingredient.material,
            name = server.displayName.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE),
            lore = buildList {
                if (isOnline) {
                    add(component {
                        text("• 在线 ") with mochaGreen without italic()
                        text("${bridgeServer?.playerCount} ") with mochaText without italic()
                        text("名玩家") with mochaSubtext0 without italic()
                    })
                } else {
                    add(component {
                        text("× 离线") with mochaMaroon without italic()
                    })
                }
                add(Component.empty())
                addAll(server.description.map {
                    it.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                })
                add(Component.empty())
                add(component {
                    text("左键 ") with mochaLavender without italic()
                    text("传送至此处") with mochaText without italic()
                })
            },
            modifier = Modifier.clickable {
                if (clickType != ClickType.LEFT) return@clickable
                if (!isOnline) return@clickable
                submitAsync {
                    player.transferServer(server.bridgeId)
                }
                sync {
                    player.closeInventory()
                }
            }
        )
        println("---------- Server end ----------")
    }

    private enum class AutoJoinState {
        LOADING, ENABLED, DISABLED
    }

    @Suppress("FunctionName")
    @Composable
    private fun AutoJoin() {
        println("---------- AutoJoin start ----------")
        val player = LocalPlayer.current
        var state by remember { mutableStateOf(LOADING) }
        LaunchedEffect(Unit) {
            println("---------- LaunchedEffect start ----------")
            val options = OptionsManager.getOptions(player.uniqueId)
            val entry = options?.getEntry(AUTO_JOIN_DESCRIPTOR)
            if (options == null || entry == null || !entry.value) {
                println("AutoJoin disabled: ${player.name}")
                state = DISABLED
                return@LaunchedEffect
            }
            state = ENABLED
            println("AutoJoin enabled: ${player.name}")
            println("---------- LaunchedEffect end ----------")
        }

        Item(
            material = Material.TRIPWIRE_HOOK,
            name = when (state) {
                LOADING -> component {
                    text("正在加载...") with mochaSubtext0 without italic()
                }

                ENABLED -> component {
                    text("自动加入 ") with mochaText without italic()
                    text("开") with mochaGreen without italic()
                }

                DISABLED -> component {
                    text("自动加入 ") with mochaText without italic()
                    text("关") with mochaMaroon without italic()
                }
            },
            enchantmentGlint = state == ENABLED,
            lore = if (state == LOADING) emptyList() else buildList {
                add(component {
                    text("下次进入时，自动传送上次选择的服务器") with mochaSubtext0 without italic()
                })
                add(Component.empty())
                add(component {
                    text("左键 ") with mochaLavender without italic()
                    if (state == DISABLED) {
                        text("开启") with mochaText without italic()
                    } else {
                        text("关闭") with mochaText without italic()
                    }
                })
            },
            modifier = Modifier.clickable {
                if (clickType != ClickType.LEFT) return@clickable
                if (state == LOADING) return@clickable
                // TODO: 切换状态
                val options = OptionsManager.getOptionsOrCreate(player.uniqueId)
                if (state == DISABLED) {
                    options.setEntry(AUTO_JOIN_DESCRIPTOR, true)
                    state = ENABLED
                } else {
                    options.setEntry(AUTO_JOIN_DESCRIPTOR, false)
                    state = DISABLED
                }
                options.save()
                player.playSound(UI_SUCCEED_SOUND)
            }
        )
        println("---------- AutoJoin end ----------")
    }
}