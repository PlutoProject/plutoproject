package ink.pmc.transfer

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.text
import ink.pmc.utils.visual.*

val DESTINATION_NOT_EXISTED = component {
    text("无法传送，ID 为 ") with mochaMaroon
    text("<id> ") with mochaText
    text("的服务器未找到") with mochaMaroon
}

val TRANSFER_FAILED_CONDITIONAL = component {
    text("传送失败，条件检测未通过") with mochaMaroon
    text("对于部分服务器，可能需要装载特定的客户端才可以连接") with mochaSubtext0
}

val TRANSFER_FAILED_SERVER_MAINTENACE = component {
    text("传送失败，目标服务器处于维护状态") with mochaMaroon
    text("你可以多加关注管理组的通知，当我们准备好后就会开放") with mochaSubtext0
}

val TRANSFER_FAILED_SERVER_OFFLINE = component {
    text("传送失败，目标服务器已离线") with mochaMaroon
    text("这可能是一个服务器内部问题，请报告给管理员") with mochaSubtext0
}

val TRANSFER_SUCCEED = component {
    text("正在将你传送至 ") with mochaText
    text("<name>") with mochaFlamingo
    text("...") with mochaText
}

val TRANSFER_OTHER_SUCCEED = component {
    text("正在将 ") with mochaText
    text("<player> ") with mochaYellow
    text("传送至 ") with mochaText
    text("<name>") with mochaFlamingo
    text("...") with mochaText
}