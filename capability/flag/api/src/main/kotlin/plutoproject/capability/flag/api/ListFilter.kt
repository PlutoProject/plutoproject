package plutoproject.capability.flag.api

import java.util.UUID

/**
 * 描述 [FlagRegistry.list] 返回哪些条目的过滤器，与 `/flag list` 命令的 flag 一一对应。
 *
 * [Player] 和 [World] 的目标是可选的：目标为 null 时列出该类 scope 下设置过的所有
 * 原始条目；目标非 null 时解析该目标的生效值。
 */
sealed interface ListFilter {
    data object All : ListFilter

    data object Server : ListFilter

    data object Global : ListFilter

    data class Player(val uuid: UUID? = null) : ListFilter

    data class World(val worldName: String? = null) : ListFilter
}
