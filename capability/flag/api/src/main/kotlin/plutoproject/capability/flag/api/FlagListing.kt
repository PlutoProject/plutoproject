package plutoproject.capability.flag.api

/**
 * [FlagRegistry.list] 的结果。具体的子类型由使用的 [ListFilter] 决定：
 *
 * - [ListFilter.All] 产生 [Overview]：每个已注册 key 及其当前设置过的 scope（含值）。
 * - 带具体目标的过滤器（[ListFilter.Server]、[ListFilter.Global]、带 UUID 的
 *   [ListFilter.Player]、带名字的 [ListFilter.World]）产生 [Effective]：每个已注册
 *   key 在该目标上经 fallback 链解析出的值，以及实际命中的 scope。
 *   [EffectiveEntry.hitScope] 为 null 表示任何 scope 都没有设置，使用注册的默认值。
 * - 裸类别过滤器（不带 UUID 的 [ListFilter.Player]、不带名字的 [ListFilter.World]）
 *   产生 [RawEntries]：该类 scope 下设置过的所有原始条目，不做 fallback 解析。
 *
 * 值以序列化后的字符串形式携带，需要时用 [FlagKey.type] 解析。
 */
sealed interface FlagListing {
    data class Overview(val entries: List<OverviewEntry>) : FlagListing

    data class Effective(val entries: List<EffectiveEntry>) : FlagListing

    data class RawEntries(val entries: List<RawEntry>) : FlagListing
}

data class ScopeSetting(val scope: Scope, val value: String)

data class OverviewEntry(val key: FlagKey<*>, val settings: List<ScopeSetting>)

data class EffectiveEntry(val key: FlagKey<*>, val value: String, val hitScope: Scope?)

data class RawEntry(val key: FlagKey<*>, val scope: Scope, val value: String)
