package ink.pmc.menu.messages

import ink.pmc.advkt.component.*
import ink.pmc.essentials.DEFAULT_ECONOMY_SYMBOL
import ink.pmc.utils.visual.*
import net.kyori.adventure.text.Component

val YUME_MAIN_TITLE = component {
    text("手账")
}

val YUME_MAIN_TAB_LORE = listOf(
    Component.empty(),
    component {
        text("左键 ") with mochaLavender without italic()
        text("切换至该选项卡") with mochaText without italic()
    }
)

val YUME_MAIN_ITEM_COMMON = component {
    text("常用功能") with mochaText without italic()
}

val YUME_MAIN_ITEM_HOME = component {
    text("明灯") with mochaYellow without italic()
}

val YUME_MAIN_ITEM_HOME_LORE = listOf(
    component {
        text("在长夜中为你指明归家路的一盏灯。") with mochaSubtext0 without italic()
    },
    Component.empty(),
    component {
        text("左键 ") with mochaLavender without italic()
        text("传送至首选的家") with mochaText without italic()
    },
    component {
        text("右键 ") with mochaLavender without italic()
        text("打开家列表") with mochaText without italic()
    },
)

val YUME_MAIN_ITEM_HOME_LORE_NO_PREFER = listOf(
    Component.empty(),
    component {
        text("你还没有首选的家，") with mochaRed without italic()
    },
    component {
        text("请在编辑家页面中点击「设为首选」") with mochaRed without italic()
    },
)

val YUME_MAIN_ITEM_HOME_RTP = component {
    text("神奇水晶") with mochaMauve without italic()
}

val YUME_MAIN_ITEM_HOME_RTP_LORE = listOf(
    component {
        text("具有魔力的紫水晶，") with mochaSubtext0 without italic()
    },
    component {
        text("可以带你去世界上的另一个角落。") with mochaSubtext0 without italic()
    },
    /*
    component {
        text("但若要借助它的力量，可能需要付出一些代价。") with mochaSubtext0 without italic()
    },
     */
    Component.empty(),
    component {
        text("左键 ") with mochaLavender without italic()
        text("进行随机传送 ") with mochaText without italic()
        text("(2$DEFAULT_ECONOMY_SYMBOL)") with mochaSubtext0 without italic()
    },
)

val YUME_MAIN_WIKI = component {
    text("点此打开星社百科") with mochaBlue with underlined() with openUrl("https://wiki.plutomc.club/")
}

val YUME_MAIN_ITEM_WIKI = component {
    text("星社百科") with mochaText without italic()
}

val YUME_MAIN_ITEM_WIKI_LORE = listOf(
    Component.empty(),
    component {
        text("左键 ") with mochaLavender without italic()
        text("获取百科链接") with mochaText without italic()
    }
)