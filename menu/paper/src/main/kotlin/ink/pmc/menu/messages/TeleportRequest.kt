package ink.pmc.menu.messages

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.essentials.config.EssentialsConfig
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.visual.*
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.koin.java.KoinJavaComponent.getKoin

val TR_TITLE = component {
    text("选择玩家")
}

val TR_EMPTY = component {
    text("暂时没有其他玩家...") with mochaText without italic()
}

val TR_LOADING = component {
    text("加载中...") with mochaText without italic()
}

val TR_PLAYER = component {
    text("<player>") with mochaFlamingo without italic()
}

val TR_PLAYER_SENT = component {
    text("√ 已发送") with mochaGreen without italic()
}

private val TR_PLAYER_INFO = component {
    text("<world> <x>, <y>, <z>") with mochaSubtext0 without italic()
}

private val essentialsConfig = getKoin().get<EssentialsConfig>()

@Suppress("FunctionName")
fun TR_PLAYER_LORE(player: Player) = listOf(
    TR_PLAYER_INFO
        .replace("<world>", essentialsConfig.WorldAliases()[player.world])
        .replace("<x>", player.location.blockX)
        .replace("<y>", player.location.blockY)
        .replace("<z>", player.location.blockZ),
    Component.empty(),
    component {
        text("左键 ") with mochaLavender without italic()
        text("请求传送至其位置") with mochaText without italic()
    },
    component {
        text("右键 ") with mochaLavender without italic()
        text("请求其传送至你这里") with mochaText without italic()
    }
)

val TR_PAGING = component {
    text("页 <curr>/<total>") with mochaText without italic()
}

val TR_PAGING_LORE = listOf(
    Component.empty(),
    component {
        text("左键 ") with mochaLavender without italic()
        text("下一页") with mochaText without italic()
    },
    component {
        text("右键 ") with mochaLavender without italic()
        text("上一页") with mochaText without italic()
    },
)