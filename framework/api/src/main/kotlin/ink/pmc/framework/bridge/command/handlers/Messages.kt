package ink.pmc.framework.bridge.command.handlers

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.text
import ink.pmc.framework.chat.mochaMaroon
import ink.pmc.framework.chat.mochaText

val bridgePlayerNotFound = component {
    text("玩家 ") with mochaMaroon
    text("<player> ") with mochaText
    text("未找到") with mochaMaroon
}

val bridgeServerNotFound = component {
    text("服务器 ") with mochaMaroon
    text("<server> ") with mochaText
    text("未找到") with mochaMaroon
}