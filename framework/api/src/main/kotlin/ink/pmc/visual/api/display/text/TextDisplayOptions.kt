package ink.pmc.visual.api.display.text

import ink.pmc.framework.utils.chat.rgbaColor
import ink.pmc.visual.api.display.DisplayOptions
import org.bukkit.entity.Display.Billboard
import org.bukkit.entity.TextDisplay.TextAlignment
import java.awt.Color

@Suppress("UNUSED")
class TextDisplayOptions(
    override val billboard: Billboard = Billboard.FIXED,
    val alignment: TextAlignment = TextAlignment.CENTER,
    val background: Color = 0x40000000.rgbaColor,
    val isDefaultBackground: Boolean = false,
    val lineWidth: Int = 200,
    val isSeeThrough: Boolean = false,
    val shadow: Boolean = false,
    val opacity: UInt = 255u
) : DisplayOptions