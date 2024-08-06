package ink.pmc.utils.chat

import java.awt.Color

val Int.rgbaColor: Color
    get() {
        val a = (this shr 24) and 0xFF
        val r = (this shr 16) and 0xFF
        val g = (this shr 8) and 0xFF
        val b = this and 0xFF
        return Color(r, g, b, a)
    }