package ink.pmc.interactive.examples

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.advkt.send
import ink.pmc.interactive.api.inventory.components.Item
import ink.pmc.interactive.api.inventory.components.VerticalGrid
import ink.pmc.interactive.api.inventory.components.canvases.Chest
import ink.pmc.interactive.api.inventory.modifiers.Modifier
import ink.pmc.interactive.api.inventory.modifiers.click.clickable
import ink.pmc.interactive.api.inventory.modifiers.fillMaxSize
import ink.pmc.utils.visual.mochaText
import net.kyori.adventure.text.Component
import org.bukkit.Material

@Suppress("UNUSED")
class ExampleScreen4 : Screen {

    override val key: ScreenKey = "interactive_example_5"

    @Composable
    override fun Content() {
        Chest(title = Component.text("ExampleScreen5")) {
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

}