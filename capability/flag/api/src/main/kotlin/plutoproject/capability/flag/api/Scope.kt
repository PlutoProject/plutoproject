package plutoproject.capability.flag.api

import java.util.UUID

/**
 * flag 值的设置范围。[SERVER] 和未显式指定服务器标识的 [World] 均指本机服务器，
 * 标识由 registry 内部填充。
 *
 * 在 Velocity 上不允许使用 world scope：代理端没有世界上下文。
 */
sealed interface Scope {
    data object GLOBAL : Scope

    /**
     * 服务器 scope。[serverId] 为 null 表示本机服务器；非 null 时，传给 registry
     * 操作的值必须与本机服务器标识一致。
     */
    data class Server(val serverId: String? = null) : Scope

    data class Player(val uuid: UUID) : Scope

    /**
     * [serverId] 所标识服务器上的世界 scope。[serverId] 为 null 表示本机服务器；
     * 非 null 时，传给 registry 操作的值必须与本机服务器标识一致。
     */
    data class World(val worldName: String, val serverId: String? = null) : Scope

    companion object {
        val SERVER: Scope = Server(null)

        fun player(uuid: UUID): Player = Player(uuid)

        fun world(worldName: String): World = World(worldName)
    }
}
