package ink.pmc.essentials

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.text
import ink.pmc.utils.visual.mochaFlamingo
import ink.pmc.utils.visual.mochaMaroon
import ink.pmc.utils.visual.mochaPink
import ink.pmc.utils.visual.mochaText
import net.kyori.adventure.text.Component

val GM_SURVIVAL = Component.text("生存模式")

val GM_CREATIVE = Component.text("创造模式")

val GM_ADVENTURE = Component.text("冒险模式")

val GM_SPECTATOR = Component.text("旁观模式")

val COMMAND_GM_SUCCCEED = component {
    text("已将游戏模式切换为 ") with mochaPink
    text("<gamemode>") with mochaText
}

val COMMAND_GM_OTHER_SUCCCEED = component {
    text("已将 ") with mochaPink
    text("<player> ") with mochaFlamingo
    text("的游戏模式切换为 ") with mochaPink
    text("<gamemode>") with mochaText
}

val COMMAND_GM_FAILED = component {
    text("你已经处于该模式了") with mochaMaroon
}

val COMMAND_GM_FAILED_OTHER = component {
    text("该玩家已经处于该模式了") with mochaMaroon
}

val COMMAND_ALIGN_SUCCEED = component {
    text("已对齐你的视角和位置") with mochaPink
}

val COMMAND_ALIGN_POS_SUCCEED = component {
    text("已对齐你的位置") with mochaPink
}

val COMMAND_ALIGN_VIEW_SUCCEED = component {
    text("已对齐你的视角") with mochaPink
}