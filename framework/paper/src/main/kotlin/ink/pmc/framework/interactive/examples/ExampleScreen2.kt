package ink.pmc.framework.interactive.examples

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.framework.interactive.InteractiveScreen
import ink.pmc.framework.interactive.LocalPlayer
import ink.pmc.framework.interactive.inventory.*
import ink.pmc.framework.interactive.inventory.canvas.Chest
import ink.pmc.framework.interactive.inventory.click.clickable
import ink.pmc.framework.interactive.inventory.jetpack.Arrangement
import ink.pmc.framework.interactive.inventory.layout.Box
import ink.pmc.framework.interactive.inventory.layout.Column
import ink.pmc.framework.interactive.inventory.layout.Row
import ink.pmc.framework.utils.concurrent.submitAsync
import ink.pmc.framework.utils.visual.mochaRed
import ink.pmc.framework.utils.visual.mochaSubtext0
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ExampleScreen2 : InteractiveScreen() {

    @Composable
    override fun Content() {
        Chest(
            title = Component.text("测试页面 2"),
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                InnerContents()
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun InnerContents() {
        val player = LocalPlayer.current
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