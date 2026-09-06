package plutoproject.capability.flag.common

import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.ScanArgs
import io.lettuce.core.ScanCursor
import plutoproject.capability.redis.api.RedisConnection
/**
 * flag 系统的存储边界。每个 scope 一个 Redis hash，flag key 作为 hash field。
 * 所有方法均为挂起函数。
 */
internal interface FlagStore {
    suspend fun hashValues(scopeHash: String): Map<String, String>

    suspend fun hashValue(scopeHash: String, field: String): String?

    suspend fun setValue(scopeHash: String, field: String, value: String)

    /**
     * 清除指定 hash 上的一个 field，返回此前是否存在该值。
     */
    suspend fun clearValue(scopeHash: String, field: String): Boolean

    suspend fun scanScopeHashes(match: String): List<String>
}

@OptIn(ExperimentalLettuceCoroutinesApi::class)
internal class LettuceFlagStore(connection: RedisConnection) : FlagStore {
    private val coroutines = connection.coroutines

    override suspend fun hashValues(scopeHash: String): Map<String, String> {
        val values = mutableMapOf<String, String>()
        coroutines.hgetall(scopeHash).collect { values[it.key] = it.value }
        return values
    }

    override suspend fun hashValue(scopeHash: String, field: String): String? = coroutines.hget(scopeHash, field)

    override suspend fun setValue(scopeHash: String, field: String, value: String) {
        coroutines.hset(scopeHash, field, value)
    }

    override suspend fun clearValue(scopeHash: String, field: String): Boolean {
        val removed = (coroutines.hdel(scopeHash, field) ?: 0L) > 0
        // hash 清空后删除它，保持命名空间扫描结果干净。
        if (removed && (coroutines.hlen(scopeHash) ?: 0L) == 0L) {
            coroutines.del(scopeHash)
        }
        return removed
    }

    override suspend fun scanScopeHashes(match: String): List<String> {
        val hashes = mutableListOf<String>()
        val args = ScanArgs.Builder.matches(match).limit(100)
        var cursor = ScanCursor.of("0")
        while (true) {
            val result = coroutines.scan(cursor, args)!!
            hashes += result.keys
            if (result.isFinished) break
            cursor = ScanCursor.of(result.cursor)
        }
        return hashes
    }
}
