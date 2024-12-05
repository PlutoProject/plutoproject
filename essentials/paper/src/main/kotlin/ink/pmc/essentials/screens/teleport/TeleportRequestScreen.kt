package ink.pmc.essentials.screens.teleport

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.essentials.COMMAND_TPAHERE_SUCCEED
import ink.pmc.essentials.COMMAND_TPA_SUCCEED
import ink.pmc.essentials.api.teleport.TeleportDirection
import ink.pmc.essentials.api.teleport.TeleportManager
import ink.pmc.framework.interactive.LocalPlayer
import ink.pmc.framework.interactive.inventory.Item
import ink.pmc.framework.interactive.inventory.ItemSpacer
import ink.pmc.framework.interactive.inventory.Modifier
import ink.pmc.framework.interactive.inventory.click.clickable
import ink.pmc.framework.interactive.inventory.layout.list.ListMenu
import ink.pmc.framework.utils.chat.DURATION
import ink.pmc.framework.utils.chat.UI_SUCCEED_SOUND
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.dsl.itemStack
import ink.pmc.framework.utils.time.ticks
import ink.pmc.framework.utils.visual.*
import ink.pmc.framework.utils.world.aliasOrName
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.meta.SkullMeta
import kotlin.time.Duration.Companion.seconds

class TeleportRequestScreen : ListMenu<Player, TeleportRequestScreenModel>() {
    override val key: ScreenKey = "essentials_teleport_request"

    @Composable
    override fun modelProvider(): TeleportRequestScreenModel {
        val player = LocalPlayer.current
        return TeleportRequestScreenModel(player)
    }

    @Composable
    override fun MenuLayout() {
        LocalListMenuOptions.current.title = Component.text("选择玩家")
        val model = LocalListMenuModel.current
        LaunchedEffect(model.onlinePlayers.size) {
            model.loadPageContents()
        }
        super.MenuLayout()
    }

    @Composable
    override fun Element(obj: Player) {
        val model = LocalListMenuModel.current
        val options = LocalListMenuOptions.current
        val coroutineScope = rememberCoroutineScope()
        val player = LocalPlayer.current
        val navigator = LocalNavigator.currentOrThrow

        var world by remember(obj) { mutableStateOf(obj.location.world.aliasOrName) }
        var x by remember(obj) { mutableStateOf(obj.location.blockX) }
        var y by remember(obj) { mutableStateOf(obj.location.blockY) }
        var z by remember(obj) { mutableStateOf(obj.location.blockZ) }

        LaunchedEffect(obj) {
            while (true) {
                world = obj.location.world.aliasOrName
                x = obj.location.blockX
                y = obj.location.blockY
                z = obj.location.blockZ
                delay(5.ticks)
            }
        }

        if (model.isRequestSent && model.requestSentTo != obj) {
            ItemSpacer()
            return
        }

        Item(
            itemStack = itemStack(Material.PLAYER_HEAD) {
                displayName = if (model.isRequestSent) component {
                    text("√ 已发送") with mochaGreen without italic()
                } else component {
                    text(obj.name) with mochaFlamingo without italic()
                }
                lore(
                    if (model.isRequestSent) {
                        emptyList()
                    } else buildList {
                        add(component {
                            text("$world $x, $y, $z") with mochaSubtext0 without italic()
                        })
                        add(Component.empty())
                        add(component {
                            text("左键 ") with mochaLavender without italic()
                            text("请求传送至其位置") with mochaText without italic()
                        })
                        add(component {
                            text("右键 ") with mochaLavender without italic()
                            text("请求其传送至你这里") with mochaText without italic()
                        })
                    }
                )
                meta {
                    this as SkullMeta
                    playerProfile = obj.playerProfile
                    setEnchantmentGlintOverride(model.requestSentTo == obj)
                }
            },
            modifier = Modifier.clickable {
                if (model.isRequestSent || model.requestSentTo != null) return@clickable
                if (TeleportManager.hasUnfinishedRequest(player)) return@clickable
                val direction = when (clickType) {
                    ClickType.LEFT -> TeleportDirection.GO
                    ClickType.RIGHT -> TeleportDirection.COME
                    else -> return@clickable
                }
                val message = when (direction) {
                    TeleportDirection.GO -> COMMAND_TPA_SUCCEED
                    TeleportDirection.COME -> COMMAND_TPAHERE_SUCCEED
                }
                TeleportManager.createRequest(player, obj, direction)
                model.isRequestSent = true
                model.requestSentTo = obj
                options.centerBackground = true
                coroutineScope.launch {
                    delay(1.seconds)
                    navigator.pop()
                }
                player.playSound(UI_SUCCEED_SOUND)
                player.sendMessage(
                    message
                        .replace("<player>", obj.name)
                        .replace("<expire>", DURATION(TeleportManager.defaultRequestOptions.expireAfter))
                )
            }
        )
    }
}