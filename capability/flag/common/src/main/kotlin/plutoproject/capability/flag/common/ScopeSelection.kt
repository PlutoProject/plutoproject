package plutoproject.capability.flag.common

import plutoproject.capability.flag.api.FlagKey
import plutoproject.capability.flag.api.FlagRegistry
import plutoproject.capability.flag.api.Scope

/**
 * get/list 命令的 scope flag 解析结果。[Player.token] 是玩家名或 UUID 的原始输入，
 * 由平台侧负责解析成 UUID；[World.worldName] 为 null 表示裸 `--world`。
 */
sealed interface ScopeSelection {
    data object None : ScopeSelection

    data object Global : ScopeSelection

    data object Server : ScopeSelection

    data class Player(val token: String?) : ScopeSelection

    data class World(val worldName: String?) : ScopeSelection

    /** 指定了多种 scope。 */
    data object Conflict : ScopeSelection

    /** 输入中包含无法识别的内容。 */
    data object Invalid : ScopeSelection
}

/**
 * 解析 get/list 命令尾部的 scope flag（`--global`、`--server`、`--player [玩家]`、
 * `--world [世界]`）。value 形式的 flag 允许不带值，含义由调用方解释。
 */
fun parseScopeSelection(input: String?, allowWorld: Boolean): ScopeSelection {
    if (input.isNullOrBlank()) return ScopeSelection.None
    val tokens = input.trim().split(WHITESPACE)
    var selection: ScopeSelection = ScopeSelection.None
    var index = 0
    while (index < tokens.size) {
        val next = when (val token = tokens[index]) {
            "--global" -> ScopeSelection.Global
            "--server" -> ScopeSelection.Server
            "--player" -> {
                val value = tokens.getOrNull(index + 1)?.takeIf { !it.startsWith("--") }
                if (value != null) index++
                ScopeSelection.Player(value)
            }
            "--world" -> {
                if (!allowWorld) return ScopeSelection.Invalid
                val value = tokens.getOrNull(index + 1)?.takeIf { !it.startsWith("--") }
                if (value != null) index++
                ScopeSelection.World(value)
            }
            else -> return ScopeSelection.Invalid
        }
        if (selection != ScopeSelection.None) return ScopeSelection.Conflict
        selection = next
        index++
    }
    return selection
}

private val WHITESPACE = Regex("\\s+")

/**
 * 用序列化后的字符串设置 flag 值，字符串与 [FlagKey.type] 不匹配时返回 false。
 */
suspend fun FlagRegistry.setFromSerialized(key: FlagKey<*>, serialized: String, scope: Scope): Boolean {
    val value = key.type.deserialize(serialized) ?: return false
    @Suppress("UNCHECKED_CAST")
    set(key as FlagKey<Any>, value as Any, scope)
    return true
}
