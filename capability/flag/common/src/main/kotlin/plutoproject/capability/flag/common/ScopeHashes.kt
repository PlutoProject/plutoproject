package plutoproject.capability.flag.common

import java.util.UUID
import plutoproject.capability.flag.api.Scope

const val FLAG_KEY_PREFIX = "plutoproject.flag."
const val RESERVED_GLOBAL_IDENTIFIER = "_global"

internal const val GLOBAL_HASH_SUFFIX = "global"

internal fun globalScopeHash(): String = "$FLAG_KEY_PREFIX$GLOBAL_HASH_SUFFIX"

internal fun serverScopeHash(serverIdentifier: String): String = "${FLAG_KEY_PREFIX}server.$serverIdentifier"

internal fun worldScopeHash(serverIdentifier: String, worldName: String): String =
    "${FLAG_KEY_PREFIX}world.$serverIdentifier.$worldName"

internal fun playerScopeHash(uuid: UUID): String = "${FLAG_KEY_PREFIX}player.$uuid"

/**
 * 校验一个 scope 能否在以 [serverIdentifier] 为本机标识的节点上进行 registry 操作：
 * server 和 world scope 必须指向本机服务器，且 world scope 需要平台支持。
 */
fun validateScope(scope: Scope, serverIdentifier: String, allowWorldScope: Boolean) {
    when (scope) {
        Scope.GLOBAL -> Unit
        is Scope.Server -> require(scope.serverId == null || scope.serverId == serverIdentifier) {
            "Server flag scopes only accept the local server identifier, got ${scope.serverId}"
        }
        is Scope.Player -> Unit
        is Scope.World -> {
            check(allowWorldScope) { "World flag scopes are not supported on this platform" }
            require(scope.serverId == null || scope.serverId == serverIdentifier) {
                "World flag scopes only accept the local server identifier, got ${scope.serverId}"
            }
        }
    }
}

/**
 * 把 scope hash（如 `plutoproject.flag.player.<uuid>` 这样的 Redis key）解析回对应的
 * [Scope]。不属于 flag 命名空间或格式非法的 hash 返回 null。
 */
fun scopeHashToScope(hash: String): Scope? {
    val rest = hash.removePrefix(FLAG_KEY_PREFIX)
    return when {
        rest == GLOBAL_HASH_SUFFIX -> Scope.GLOBAL
        rest.startsWith("server.") -> {
            val serverId = rest.removePrefix("server.")
            if (serverId.isEmpty()) null else Scope.Server(serverId)
        }
        rest.startsWith("world.") -> {
            val body = rest.removePrefix("world.")
            val dot = body.indexOf('.')
            if (dot <= 0 || dot == body.lastIndex) {
                null
            } else {
                Scope.World(worldName = body.substring(dot + 1), serverId = body.substring(0, dot))
            }
        }
        rest.startsWith("player.") -> runCatching {
            Scope.Player(UUID.fromString(rest.removePrefix("player.")))
        }.getOrNull()
        else -> null
    }
}

/**
 * 按 player、world、server、global 的顺序排序 scope，用于保持列表输出的稳定。
 */
fun scopeSortOrder(scope: Scope): Int = when (scope) {
    is Scope.Player -> 0
    is Scope.World -> 1
    is Scope.Server -> 2
    Scope.GLOBAL -> 3
}
