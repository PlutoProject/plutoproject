package ink.pmc.framework.interactive.inventory.layout.list

import net.kyori.adventure.text.Component
import org.bukkit.Material

data class ListMenuOptions(
    val title: Component = Component.empty(),
    val rows: Int = 6,
    val topBorder: Boolean = true,
    val bottomBorder: Boolean = true,
    val leftBorder: Boolean = true,
    val rightBorder: Boolean = true,
    val navigatorWarn: Boolean = true,
    val previousTurnerIcon: Material = Material.ARROW,
    val nextTurnerIcon: Material = Material.ARROW,
)