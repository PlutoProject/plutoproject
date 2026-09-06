package plutoproject.capability.flag.common

import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration

/**
 * 整个 scope hash 的本地缓存，以 scope hash 名为键。缓存中没有条目表示本节点从未
 * 读取过该 hash；已存在但过期的条目与缺失同样处理。负面信息（某个 hash 中未设置
 * 某 flag）随 hash 内容一起缓存。
 */
internal class FlagCache(
    private val ttl: Duration,
    private val now: () -> Long = System::currentTimeMillis,
) {
    private data class Entry(val values: Map<String, String>, val expiresAt: Long)

    private val hashes = ConcurrentHashMap<String, Entry>()

    /**
     * 返回缓存的 hash 内容；hash 未知或已过期时返回 null。
     */
    fun peek(scopeHash: String): Map<String, String>? {
        val entry = hashes[scopeHash] ?: return null
        if (now() >= entry.expiresAt) {
            hashes.remove(scopeHash)
            return null
        }
        return entry.values
    }

    fun put(scopeHash: String, values: Map<String, String>) {
        hashes[scopeHash] = Entry(values, now() + ttl.inWholeMilliseconds)
    }

    fun cachedScopeHashes(): List<String> = hashes.keys().toList()
}
