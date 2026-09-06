package plutoproject.capability.flag.common

import plutoproject.capability.flag.api.Scope

/**
 * 把 scope 渲染成命令输出用的展示文本，例如 `global`、`server(survival-1)`、
 * `world(survival-1.world)` 或 `player(<uuid>)`。
 */
fun Scope.displayText(): String = when (this) {
    Scope.GLOBAL -> "global"
    is Scope.Server -> serverId?.let { "server($it)" } ?: "server"
    is Scope.Player -> "player($uuid)"
    is Scope.World -> serverId?.let { "world($it.$worldName)" } ?: "world($worldName)"
}
