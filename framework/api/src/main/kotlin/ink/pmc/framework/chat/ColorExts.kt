package ink.pmc.framework.chat

import net.kyori.adventure.text.format.TextColor
import java.awt.Color

inline val Int.rgbaColor: Color
    get() {
        return Color(
            (this shr 16) and 0xFF,
            (this shr 8) and 0xFF,
            this and 0xFF,
            (this shr 24) and 0xFF
        )
    }

@Suppress("UNUSED", "NOTHING_TO_INLINE")
inline fun Color.toTextColor(): TextColor {
    return TextColor.color(red, green, blue)
}