package ink.pmc.common.rpc

import ink.pmc.common.utils.visual.mochaFlamingo
import ink.pmc.common.utils.visual.mochaMaroon
import ink.pmc.common.utils.visual.mochaSubtext0
import net.kyori.adventure.text.Component

val RPC_SERVER_SERVICES
    get() = Component.text("已在 gRPC 服务端注册的服务: ").color(mochaFlamingo)

val RPC_SERVER_SERVICES_EMPTY
    get() = Component.text("暂无注册的服务").color(mochaMaroon)

val RPC_SERVER_SERVICES_ENTRY
    get() = Component.text("  - <name>").color(mochaSubtext0)