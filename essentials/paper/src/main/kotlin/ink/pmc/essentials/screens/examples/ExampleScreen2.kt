package ink.pmc.essentials.screens.examples

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.interactive.inventory.canvas.LocalInvOwner
import ink.pmc.interactive.inventory.components.Item
import ink.pmc.interactive.inventory.components.Spacer
import ink.pmc.interactive.inventory.components.canvases.Chest
import ink.pmc.interactive.inventory.jetpack.Arrangement
import ink.pmc.interactive.inventory.layout.Box
import ink.pmc.interactive.inventory.layout.Column
import ink.pmc.interactive.inventory.layout.Row
import ink.pmc.interactive.inventory.modifiers.Modifier
import ink.pmc.interactive.inventory.modifiers.click.clickable
import ink.pmc.interactive.inventory.modifiers.fillMaxSize
import ink.pmc.interactive.inventory.modifiers.fillMaxWidth
import ink.pmc.interactive.inventory.modifiers.height
import ink.pmc.utils.concurrent.submitAsync
import ink.pmc.utils.visual.mochaRed
import ink.pmc.utils.visual.mochaSubtext0
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ExampleScreen2(private val player: Player) : Screen {

    override val key: ScreenKey = "essentials_example_2"

    @Composable
    override fun Content() {
        val owner = LocalInvOwner.current
        Chest(
            viewers = setOf(player),
            title = Component.text("测试页面 2"),
            modifier = Modifier.fillMaxSize(),
            onClose = { owner.exit() },
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                InnerContents()
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun InnerContents() {
        val navigator = LocalNavigator.currentOrThrow
        Box(modifier = Modifier.fillMaxWidth().height(1)) {
            Row(modifier = Modifier.fillMaxSize()) {
                repeat(9) {
                    Item(
                        material = Material.GRAY_STAINED_GLASS_PANE,
                        name = component { text("占位符") with mochaSubtext0 without italic() }
                    )
                }
            }
            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Start) {
                Item(
                    material = Material.RED_STAINED_GLASS_PANE,
                    name = component { text("返回上一页") with mochaRed without italic() },
                    modifier = Modifier.clickable {
                        navigator.pop()
                    }
                )
            }
        }
        Column(modifier = Modifier.fillMaxWidth().height(4), verticalArrangement = Arrangement.Center) {
            Row(modifier = Modifier.fillMaxWidth().height(1), horizontalArrangement = Arrangement.Center) {
                Item(
                    material = Material.APPLE,
                    name = component { text("获取一个苹果") with mochaRed without italic() },
                    modifier = Modifier.clickable {
                        submitAsync {
                            player.inventory.addItem(ItemStack(Material.APPLE))
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.fillMaxWidth().height(1))
        }
        Row(modifier = Modifier.fillMaxWidth().height(1)) {
            repeat(9) {
                Item(
                    material = Material.GRAY_STAINED_GLASS_PANE,
                    name = component { text("占位符") with mochaSubtext0 without italic() }
                )
            }
        }
    }

}