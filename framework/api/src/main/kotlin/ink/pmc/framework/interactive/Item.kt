package ink.pmc.framework.interactive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import ink.pmc.framework.interactive.canvas.Canvas
import ink.pmc.framework.interactive.layout.Layout
import ink.pmc.framework.interactive.layout.MeasureResult
import ink.pmc.framework.interactive.layout.Renderer
import ink.pmc.framework.interactive.nodes.BaseInventoryNode
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * An item to display in an inventory layout.
 *
 * @param itemStack The [ItemStack] to display.
 */
@Composable
fun Item(itemStack: ItemStack, modifier: Modifier = Modifier) {
    Layout(
        measurePolicy = { _, constraints ->
            MeasureResult(constraints.minWidth, constraints.minHeight) {}
        },
        renderer = object : Renderer {
            override fun Canvas.render(node: BaseInventoryNode) {
                for (x in 0 until node.width)
                    for (y in 0 until node.height)
                        set(x, y, itemStack)
            }
        },
        modifier = Modifier.sizeIn(minWidth = 1, minHeight = 1).then(modifier)
    )
}

/**
 * An item to display in an inventory layout.
 *
 * @param material The [Material] of the item.
 * @param name The item's display name (formatted by MiniMesssage).
 * @param amount The amount of the item.
 * @param lore The item's lore (formatted by MiniMessage).
 */
@Composable
fun Item(
    material: Material,
    name: Component = Component.empty(),
    amount: Int = 1,
    lore: List<Component> = emptyList(),
    isHideTooltip: Boolean = false,
    enchantmentGlint: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val rememberName = remember(name) { name }
    val rememberLore = remember(lore) { lore }

    val item = remember(material, name, amount, lore) {
        ItemStack(material, amount).apply {
            editMeta {
                it.displayName(rememberName)
                it.lore(rememberLore)
                it.isHideTooltip = isHideTooltip
                it.setEnchantmentGlintOverride(enchantmentGlint)
            }
        }
    }

    Item(item, modifier)
}
