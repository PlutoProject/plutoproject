package plutoproject.capability.flag.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import plutoproject.capability.flag.api.EffectiveEntry
import plutoproject.capability.flag.api.FlagKey
import plutoproject.capability.flag.api.FlagListing
import plutoproject.capability.flag.api.FlagRegistry
import plutoproject.capability.flag.api.ListFilter
import plutoproject.capability.flag.api.OverviewEntry
import plutoproject.capability.flag.api.RawEntry
import plutoproject.capability.flag.api.Scope
import plutoproject.capability.flag.api.ScopeSetting
import plutoproject.capability.flag.api.serializeDefault
import java.util.concurrent.ConcurrentHashMap

internal class FlagRegistryImpl(
    private val store: FlagStore,
    private val serverIdentifier: String,
    private val cache: FlagCache,
    private val backgroundScope: CoroutineScope,
    private val allowWorldScope: Boolean,
) : FlagRegistry {
    private val registeredKeys = ConcurrentHashMap<String, FlagKey<*>>()

    private val localServerHash = serverScopeHash(serverIdentifier)
    private val globalHash = globalScopeHash()

    override val keys: Collection<FlagKey<*>>
        get() = registeredKeys.values.toList()

    private val keysByName: Map<String, FlagKey<*>>
        get() = registeredKeys

    override fun register(key: FlagKey<*>) {
        registeredKeys[key.name] = key
    }

    override fun unregister(key: FlagKey<*>) {
        registeredKeys.remove(key.name)
    }

    override fun <T : Any> get(key: FlagKey<T>, scope: Scope?): T {
        scope?.let { validateScope(it, serverIdentifier, allowWorldScope) }
        for ((hash, _) in scopeChain(scope)) {
            val values = cache.peek(hash)
            if (values == null) {
                backgroundScope.launch { refreshHash(hash) }
                continue
            }
            val parsed = values[key.name]?.let { key.type.deserialize(it) }
            if (parsed != null) return parsed
        }
        return key.default
    }

    override suspend fun <T : Any> getDirect(key: FlagKey<T>, scope: Scope?): T {
        scope?.let { validateScope(it, serverIdentifier, allowWorldScope) }
        for ((hash, _) in scopeChain(scope)) {
            val parsed = store.hashValue(hash, key.name)?.let { key.type.deserialize(it) }
            if (parsed != null) return parsed
        }
        return key.default
    }

    override suspend fun <T : Any> set(key: FlagKey<T>, value: T, scope: Scope) {
        val hash = targetHash(scope)
        store.setValue(hash, key.name, key.type.serialize(value))
        refreshHash(hash)
    }

    override suspend fun clear(key: FlagKey<*>, scope: Scope): Boolean {
        val hash = targetHash(scope)
        val removed = store.clearValue(hash, key.name)
        if (removed) {
            refreshHash(hash)
        }
        return removed
    }

    override suspend fun list(filter: ListFilter): FlagListing = when (filter) {
        ListFilter.All -> listOverview()
        ListFilter.Server -> listEffective(Scope.SERVER)
        ListFilter.Global -> listEffective(Scope.GLOBAL)
        is ListFilter.Player -> filter.uuid
            ?.let { listEffective(Scope.Player(it)) }
            ?: listRaw("player.")
        is ListFilter.World -> {
            check(allowWorldScope) { "World flag scopes are not supported on this platform" }
            filter.worldName
                ?.let { listEffective(Scope.World(it)) }
                ?: listRaw("world.")
        }
    }

    /**
     * 读取的 fallback 链：hash 名与该 hash 对应 scope 的配对，从请求的 scope 向
     * global 排列。world scope 不携带玩家上下文，玩家 scope 不携带世界上下文，
     * 因此玩家读取直接回退到 server。
     */
    private fun scopeChain(scope: Scope?): List<Pair<String, Scope>> = when (scope) {
        null, Scope.SERVER, is Scope.Server -> listOf(localServerHash to Scope.SERVER, globalHash to Scope.GLOBAL)
        Scope.GLOBAL -> listOf(globalHash to Scope.GLOBAL)
        is Scope.Player -> listOf(playerScopeHash(scope.uuid) to scope, localServerHash to Scope.SERVER, globalHash to Scope.GLOBAL)
        is Scope.World -> listOf(worldScopeHash(serverIdentifier, scope.worldName) to scope, localServerHash to Scope.SERVER, globalHash to Scope.GLOBAL)
    }

    private fun targetHash(scope: Scope): String {
        validateScope(scope, serverIdentifier, allowWorldScope)
        return when (scope) {
            Scope.GLOBAL -> globalHash
            is Scope.Server -> localServerHash
            is Scope.Player -> playerScopeHash(scope.uuid)
            is Scope.World -> worldScopeHash(serverIdentifier, scope.worldName)
        }
    }

    private suspend fun refreshHash(hash: String) {
        cache.put(hash, store.hashValues(hash))
    }

    internal suspend fun preload() {
        refreshHash(globalHash)
        refreshHash(localServerHash)
    }

    /**
     * 让缓存中的每个 hash 保持新鲜。player 和 world scope 的 hash 在本节点读取过
     * 一次后进入缓存，之后持续刷新。
     */
    internal fun startRefreshLoop(scope: CoroutineScope, ttl: kotlin.time.Duration) {
        scope.launch {
            while (isActive) {
                delay(ttl)
                for (hash in cache.cachedScopeHashes()) {
                    refreshHash(hash)
                }
            }
        }
    }

    private suspend fun listOverview(): FlagListing.Overview {
        val settingsByName = HashMap<String, MutableList<ScopeSetting>>()
        for (hash in store.scanScopeHashes("$FLAG_KEY_PREFIX*")) {
            val scope = scopeHashToScope(hash) ?: continue
            for ((field, value) in store.hashValues(hash)) {
                settingsByName.getOrPut(field) { mutableListOf() } += ScopeSetting(scope, value)
            }
        }
        return FlagListing.Overview(
            keys.map { key ->
                val settings = settingsByName[key.name].orEmpty().sortedBy { scopeSortOrder(it.scope) }
                OverviewEntry(key, settings)
            },
        )
    }

    private suspend fun listEffective(scope: Scope): FlagListing.Effective {
        validateScope(scope, serverIdentifier, allowWorldScope)
        val chain = scopeChain(scope)
        val hashValues = chain.map { (hash, _) -> store.hashValues(hash) }
        return FlagListing.Effective(
            keys.map { key ->
                val hit = chain.indices
                    .map { index -> hashValues[index][key.name]?.let { index to it } }
                    .firstOrNull { it != null }
                if (hit == null) {
                    EffectiveEntry(key, key.serializeDefault(), hitScope = null)
                } else {
                    val (index, raw) = hit
                    EffectiveEntry(key, raw, chain[index].second)
                }
            },
        )
    }

    private suspend fun listRaw(hashPrefix: String): FlagListing.RawEntries {
        val entries = mutableListOf<RawEntry>()
        for (hash in store.scanScopeHashes("$FLAG_KEY_PREFIX$hashPrefix*")) {
            val scope = scopeHashToScope(hash) ?: continue
            for ((field, value) in store.hashValues(hash)) {
                val key = keysByName[field] ?: continue
                entries += RawEntry(key, scope, value)
            }
        }
        return FlagListing.RawEntries(
            entries.sortedWith(compareBy({ scopeSortOrder(it.scope) }, { it.key.name })),
        )
    }
}
