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
import ink.pmc.framework.interactive.inventory.layout.Column
import ink.pmc.framework.interactive.inventory.layout.Menu
import ink.pmc.framework.interactive.inventory.layout.Row
import ink.pmc.framework.utils.concurrent.submitAsync
import ink.pmc.framework.utils.concurrent.sync
import ink.pmc.framework.utils.visual.*
import ink.pmc.serverselector.Ingredient
import ink.pmc.serverselector.Server
import ink.pmc.serverselector.ServerSelectorConfig
import ink.pmc.serverselector.transferServer
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
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
            rows = config.menu.rows
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
}