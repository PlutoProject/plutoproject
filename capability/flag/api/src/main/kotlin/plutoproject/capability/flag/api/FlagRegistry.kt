package plutoproject.capability.flag.api

/**
 * 运行时 flag 的中央注册表。消费方在启动时注册 [FlagKey]，并通过 [get] / [getDirect]
 * 读取值。共用同一个 Redis 的整个群组内的所有服务器看到的 flag 值是一致的。
 */
interface FlagRegistry {
    val keys: Collection<FlagKey<*>>

    fun register(key: FlagKey<*>)

    fun unregister(key: FlagKey<*>)

    /**
     * 基于本地缓存的同步读取（TTL 可配置）。不传 scope 时从本机 server scope 开始
     * fallback。从未读取过的 scope 在本次调用中视为未设置，并在后台异步加载，
     * 因此启动后的首次读取可能短暂地穿透到默认值。server 和 global scope 会预热，
     * 始终能正确解析。
     */
    fun <T : Any> get(key: FlagKey<T>, scope: Scope? = null): T

    /**
     * 挂起读取，总是直连 Redis，不经过本地缓存。
     */
    suspend fun <T : Any> getDirect(key: FlagKey<T>, scope: Scope? = null): T

    /**
     * 在 [scope] 上设置值。本机缓存立即刷新；其他节点在缓存 TTL 过期后看到新值。
     */
    suspend fun <T : Any> set(key: FlagKey<T>, value: T, scope: Scope)

    /**
     * 移除在 [scope] 上设置的值，使 fallback 链（或注册的默认值）重新生效。
     * 返回该 scope 上此前是否设置过值。
     */
    suspend fun clear(key: FlagKey<*>, scope: Scope): Boolean

    suspend fun list(filter: ListFilter = ListFilter.All): FlagListing
}
