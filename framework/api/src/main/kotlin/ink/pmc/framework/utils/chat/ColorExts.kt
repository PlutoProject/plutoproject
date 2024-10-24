package ink.pmc.framework.utils.chat

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