package plutoproject.capability.flag.paper

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.text
import ink.pmc.advkt.component.newline
import plutoproject.foundation.common.text.mochaGreen
import plutoproject.foundation.common.text.mochaMaroon
import plutoproject.foundation.common.text.mochaSubtext0
import plutoproject.foundation.common.text.mochaText

const val PERMISSION_COMMAND_FLAG = "plutoproject.flag.command.flag"

val COMMAND_FLAG_SET_SUCCEED = component {
    text("已设置 flag ") with mochaText
    text("<key> = <value>") with mochaGreen
    text("（") with mochaSubtext0
    text("<scope>") with mochaSubtext0
    text("）") with mochaSubtext0
}

val COMMAND_FLAG_CLEAR_SUCCEED = component {
    text("已清除 flag ") with mochaText
    text("<key>") with mochaGreen
    text(" 在 ") with mochaText
    text("<scope>") with mochaSubtext0
    text(" 上的设置") with mochaText
}

val COMMAND_FLAG_CLEAR_NOTHING = component {
    text("flag ") with mochaText
    text("<key>") with mochaGreen
    text(" 在 ") with mochaText
    text("<scope>") with mochaSubtext0
    text(" 上没有设置") with mochaText
}

val COMMAND_FLAG_GET_VALUE = component {
    text("flag ") with mochaText
    text("<key>") with mochaGreen
    text(" = ") with mochaText
    text("<value>") with mochaGreen
    text("（命中 ") with mochaSubtext0
    text("<scope>") with mochaSubtext0
    text("）") with mochaSubtext0
}

val COMMAND_FLAG_GET_DEFAULT = component {
    text("flag ") with mochaText
    text("<key>") with mochaGreen
    text(" 未在任何 scope 上设置，使用默认值 ") with mochaText
    text("<value>") with mochaGreen
}

val COMMAND_FLAG_LIST_HEADER = component {
    text("flag 列表：") with mochaText
}

val COMMAND_FLAG_LIST_EMPTY = component {
    text("没有已注册的 flag") with mochaSubtext0
}

val COMMAND_FLAG_UNKNOWN_KEY = component {
    text("未知的 flag ") with mochaMaroon
    text("<key>") with mochaText
    newline()
    text("使用 /flag list 查看所有已注册的 flag") with mochaSubtext0
}

val COMMAND_FLAG_INVALID_VALUE = component {
    text("无法将 ") with mochaMaroon
    text("<value>") with mochaText
    text(" 解析为 ") with mochaMaroon
    text("<type>") with mochaText
}

val COMMAND_FLAG_PLAYER_NOT_FOUND = component {
    text("无法找到玩家 ") with mochaMaroon
    text("<input>") with mochaText
}

val COMMAND_FLAG_SCOPE_CONFLICT = component {
    text("只能指定一种 scope flag") with mochaMaroon
}

val COMMAND_FLAG_INVALID_SCOPE_FLAG = component {
    text("无法识别的 scope flag，可用：--global、--server、--player [玩家]、--world [世界]") with mochaMaroon
}

val COMMAND_FLAG_GET_REQUIRES_TARGET = component {
    text("get 的 --player 和 --world 必须提供具体的玩家或世界") with mochaMaroon
}
