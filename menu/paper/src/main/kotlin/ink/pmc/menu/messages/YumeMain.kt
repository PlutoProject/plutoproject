package ink.pmc.menu.messages

import ink.pmc.advkt.component.*
import ink.pmc.essentials.DEFAULT_ECONOMY_SYMBOL
import ink.pmc.essentials.api.teleport.random.RandomTeleportManager
import ink.pmc.menu.economy
import ink.pmc.framework.utils.trimmed
import ink.pmc.framework.utils.visual.*
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

val YUME_MAIN_TITLE = component {
    text("手账")
}

val YUME_MAIN_FIRST_OPEN_PROMPT = component {
    text("小提示: 你可以使用 ") with mochaText
    keybind("key.sneak") with mochaLavender
    text(" + ") with mochaLavender
    keybind("key.swapOffhand") with mochaLavender
    text(" 或 ") with mochaText
    text("/menu ") with mochaLavender
    text("来打开「手账」") with mochaText
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
        text("你还没有首选的家，") with mochaMaroon without italic()
    },
    component {
        text("请在编辑家页面中点击「设为首选」") with mochaMaroon without italic()
    },
)

val YUME_MAIN_ITEM_SPAWN = component {
    text("伊始之处") with mochaFlamingo without italic()
}

private val SPAWN_PICKED = component {
    text("当前已选 ") with mochaSubtext0 without italic()
    text("<spawn>") with mochaText without italic()
}

private val SPAWN_LOC = component {
    text("<loc>") with mochaSubtext0 without italic()
}

private val SPAWN_OPERATION_1 = component {
    text("左键 ") with mochaLavender without italic()
    text("回到主城") with mochaText without italic()
}

private val SPAWN_OPERATION_2 = component {
    text("右键 ") with mochaLavender without italic()
    text("设置首选主城") with mochaText without italic()
}

private val SPAWN_DESC = component {
    text("于此直至天边的故事。") with mochaSubtext0 without italic()
}

val YUME_MAIN_ITEM_SPAWN_LORE = listOf(
    SPAWN_PICKED,
    SPAWN_LOC,
    SPAWN_DESC,
    Component.empty(),
    SPAWN_OPERATION_1,
    SPAWN_OPERATION_2
)

val YUME_MAIN_ITEM_SPAWN_LORE_UNPICKED = listOf(
    SPAWN_DESC,
    Component.empty(),
    SPAWN_OPERATION_1,
    SPAWN_OPERATION_2
)

val YUME_MAIN_ITEM_SPAWN_LORE_NO_PREFERRED = listOf(
    Component.empty(),
    component {
        text("你还没有首选的主城，") with mochaMaroon without italic()
    },
    component {
        text("右键点击来设置。") with mochaMaroon without italic()
    }
)

val YUME_MAIN_ITEM_TP = component {
    text("巡回列车") with mochaSapphire without italic()
}

val YUME_MAIN_ITEM_TP_LORE = listOf(
    component {
        text("拜访世界中的其他玩家。") with mochaSubtext0 without italic()
    },
    Component.empty(),
    component {
        text("左键 ") with mochaLavender without italic()
        text("发起传送请求") with mochaText without italic()
    },
)

val YUME_MAIN_ITEM_TP_EXISTED_LORE = listOf(
    Component.empty(),
    component {
        text("你还有未完成的传送请求，") with mochaMaroon without italic()
    },
    component {
        text("可使用 ") with mochaMaroon without italic()
        text("/tpcancel ") with mochaText without italic()
        text("来取消") with mochaMaroon without italic()
    },
)

val YUME_MAIN_ITEM_HOME_RTP = component {
    text("神奇水晶") with mochaMauve without italic()
}

val YUME_MAIN_RTP_COST = "${RandomTeleportManager.defaultOptions.cost.trimmed()}$DEFAULT_ECONOMY_SYMBOL"

val YUME_MAIN_ITEM_HOME_RTP_LORE = listOf(
    component {
        text("具有魔力的紫水晶，") with mochaSubtext0 without italic()
    },
    component {
        text("可以带你去世界上的另一个角落。") with mochaSubtext0 without italic()
    },
    Component.empty(),
    component {
        text("左键 ") with mochaLavender without italic()
        text("进行随机传送 ") with mochaText without italic()
        text("($YUME_MAIN_RTP_COST)") with mochaSubtext0 without italic()
    },
)

val YUME_MAIN_ITEM_HOME_RTP_COIN_NOT_ENOUGH_LORE = listOf(
    Component.empty(),
    component {
        text("货币不足，") with mochaMaroon without italic()
    },
    component {
        text("进行随机传送需要 ") with mochaMaroon without italic()
        text(YUME_MAIN_RTP_COST) with mochaText without italic()
    },
)

val YUME_MAIN_ITEM_HOME_RTP_NOT_ENABLED_LORE = listOf(
    Component.empty(),
    component { text("该世界未启用随机传送") with mochaMaroon without italic() },
)

val YUME_MAIN_ITEM_HOME_LOOKUP = component {
    text("查询模式") with mochaGreen without italic()
}

val YUME_MAIN_ITEM_HOME_LOOKUP_ENABLE = component {
    text("√ 已开启") with mochaGreen without italic()
}

val YUME_MAIN_ITEM_HOME_LOOKUP_DISABLE = component {
    text("× 已关闭") with mochaMaroon without italic()
}

val YUME_MAIN_ITEM_HOME_LOOKUP_LORE = listOf(
    component {
        text("将周围的变化一览无余，") with mochaSubtext0 without italic()
    },
    component {
        text("这对抓住做坏事的人特别有用。") with mochaSubtext0 without italic()
    },
    Component.empty(),
    component {
        text("左键 ") with mochaLavender without italic()
        text("进入查询模式") with mochaText without italic()
    },
    component {
        text("右键 ") with mochaLavender without italic()
        text("查询四周变化") with mochaText without italic()
    },
)

val YUME_MAIN_ITEM_HOME_LOOKUP_ENABLED_LORE = listOf(
    component {
        text("查询模式已开启，") with mochaSubtext0 without italic()
    },
    component {
        text("使用左键或右键点击来查询一个位置。") with mochaSubtext0 without italic()
    },
    Component.empty(),
    component {
        text("左键 ") with mochaLavender without italic()
        text("退出查询模式") with mochaText without italic()
    },
)

val YUME_MAIN_ITEMS_DAILY = component {
    text("礼记") with mochaPink without italic()
}

private val DAILY_LORE_OPERATION = component {
    text("左键 ") with mochaLavender without italic()
    text("打开礼记日历") with mochaText without italic()
}

private val DAILY_LORE_INTODUCTION = component {
    text("时光与点滴足迹。") with mochaSubtext0 without italic()
}

val YUME_MAIN_ITEMS_DAILY_LORE = listOf(
    component {
        text("× 今日尚未到访") with mochaYellow without italic()
    },
    DAILY_LORE_INTODUCTION,
    Component.empty(),
    DAILY_LORE_OPERATION
)

val YUME_MAIN_ITEMS_DAILY_LORE_CHECKED_IN = listOf(
    component {
        text("√ 今日已到访") with mochaGreen without italic()
    },
    DAILY_LORE_INTODUCTION,
    Component.empty(),
    DAILY_LORE_OPERATION
)

val YUME_MAIN_ITEM_COINS = component {
    text("货币") with mochaYellow without italic()
}

@Suppress("FunctionName")
fun YUME_MAIN_ITEM_COINS_LORE(player: Player): List<Component> {
    val balance = economy.getBalance(player).trimmed()
    return listOf(
        component {
            text("你的余额: ") with mochaSubtext0 without italic()
            text("$balance$DEFAULT_ECONOMY_SYMBOL") with mochaText without italic()
        }
    )
}

val YUME_MAIN_WIKI = component {
    text("点此打开星社百科") with mochaLavender with underlined() with openUrl("https://wiki.plutomc.club/")
}

val YUME_MAIN_ITEM_WIKI = component {
    text("星社百科") with mochaText without italic()
}

val YUME_MAIN_ITEM_WIKI_LORE = listOf(
    component {
        text("服务器的百科全书，") with mochaSubtext0 without italic()
    },
    component {
        text("里面记载了有关星社的一切。") with mochaSubtext0 without italic()
    },
    Component.empty(),
    component {
        text("左键 ") with mochaLavender without italic()
        text("获取百科链接") with mochaText without italic()
    }
)

val ITEM_DISABLED = component {
    text("关") with mochaMaroon without italic()
}

val ITEM_ENABLED = component {
    text("开") with mochaGreen without italic()
}

val MAIN_MENU_ITEM_VIEW_BOOST = component {
    text("视距拓展") with mochaText without italic()
}

private val MAIN_MENU_ITEM_VIEW_BOOST_LORE_COMMON = listOf(
    component {
        text("可让服务器为你发送至多 ") with mochaSubtext0 without italic()
        text("16 ") with mochaText without italic()
        text("视距") with mochaSubtext0 without italic()
    },
    component {
        text("使观景体验大幅提升") with mochaSubtext0 without italic()
    }
)

val MAIN_MENU_ITEM_VIEW_BOOST_LORE_DISABLED = buildList {
    addAll(MAIN_MENU_ITEM_VIEW_BOOST_LORE_COMMON)
    add(Component.empty())
    add(component {
        text("左键 ") with mochaLavender without italic()
        text("开启功能") with mochaText without italic()
    })
}

val MAIN_MENU_ITEM_VIEW_BOOST_LORE_ENABLED = listOf(
    component {
        text("请将渲染距离调整至 ") with mochaSubtext0 without italic()
        text("16 ") with mochaText without italic()
        text("或更高") with mochaSubtext0 without italic()
    },
    component {
        text("以使此功能生效") with mochaSubtext0 without italic()
    },
    Component.empty(),
    component {
        text("左键 ") with mochaLavender without italic()
        text("关闭功能") with mochaText without italic()
    }
)

val MAIN_MENU_ITEM_VIEW_BOOST_LORE_DISABLED_DUE_PING = buildList {
    addAll(MAIN_MENU_ITEM_VIEW_BOOST_LORE_COMMON)
    add(Component.empty())
    add(component {
        text("此功能仅在延迟小于 ") with mochaYellow without italic()
        text("100ms ") with mochaText without italic()
        text("时可用") with mochaYellow without italic()
    })
    add(component {
        text("可尝试切换到一个质量更好的网络接入点") with mochaYellow without italic()
    })
}

val MAIN_MENU_ITEM_VIEW_BOOST_LORE_DISABLED_DUE_VHOST = buildList {
    addAll(MAIN_MENU_ITEM_VIEW_BOOST_LORE_COMMON)
    add(Component.empty())
    add(component {
        text("你正在使用的连接线路不支持此功能") with mochaYellow without italic()
    })
    add(component {
        text("此功能仅在使用主线路时可用") with mochaYellow without italic()
    })
}