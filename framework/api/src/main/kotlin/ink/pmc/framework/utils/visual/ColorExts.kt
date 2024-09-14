package ink.pmc.framework.utils.visual

import net.kyori.adventure.text.format.TextColor
import java.awt.Color

@Suppress("UNUSED", "NOTHING_TO_INLINE")
inline fun Color.toTextColor(): TextColor {
    return TextColor.color(red, green, blue)
}