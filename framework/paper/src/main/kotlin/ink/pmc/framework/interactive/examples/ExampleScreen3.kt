package ink.pmc.framework.interactive.examples

import androidx.compose.runtime.Composable
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.advkt.send
import ink.pmc.framework.interactive.inventory.Item
import ink.pmc.framework.interactive.inventory.Modifier
import ink.pmc.framework.interactive.inventory.VerticalGrid
import ink.pmc.framework.interactive.inventory.click.clickable
import ink.pmc.framework.interactive.inventory.canvas.Chest
import ink.pmc.framework.interactive.inventory.fillMaxSize
import ink.pmc.framework.chat.mochaText
import net.kyori.adventure.text.Component
import org.bukkit.Material

@Composable
@Suppress("FunctionName")
fun ExampleScreen3() {
    Chest(title = Component.text("ExampleScreen3"), modifier = Modifier.fillMaxSize()) {
        VerticalGrid(modifier = Modifier.fillMaxSize()) {
            repeat(54) {
                Item(
                    material = Material.PAPER,
                    name = component {
                        text("测试物品") with mochaText without italic()
                    },
                    modifier = Modifier.clickable {
                        whoClicked.send {
                            text("你点击了测试物品") with mochaText
                        }
                    }
                )
            }
        }
    }
}