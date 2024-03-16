package ink.pmc.common.utils

import com.catppuccin.Palette
import net.kyori.adventure.text.Component

@Suppress("UNUSED")
val NON_PLAYER = Component.text("该命令仅限玩家使用")
    .color(Palette.MOCHA.red.toTextColor())