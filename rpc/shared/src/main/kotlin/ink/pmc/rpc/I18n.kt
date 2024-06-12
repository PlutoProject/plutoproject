package ink.pmc.rpc

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.text
import ink.pmc.utils.visual.mochaFlamingo
import ink.pmc.utils.visual.mochaMaroon
import ink.pmc.utils.visual.mochaSubtext0

val RPC_SERVER_SERVICES = component {
    text("已在 gRPC 服务端注册的服务: ") with mochaFlamingo
}

val RPC_SERVER_SERVICES_EMPTY = component {
    text("暂无注册的服务") with mochaMaroon
}

val RPC_SERVER_SERVICES_ENTRY = component {
    text("  - <name>") with mochaSubtext0
}