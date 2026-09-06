package plutoproject.capability.flag.common

import io.lettuce.core.RedisClient
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import io.lettuce.core.api.coroutines
import io.lettuce.core.api.sync.RedisCommands
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.GenericContainer
import plutoproject.capability.flag.api.ListFilter
import plutoproject.capability.flag.api.FlagListing
import plutoproject.capability.flag.api.Scope
import plutoproject.capability.flag.api.booleanFlag
import plutoproject.capability.flag.api.intFlag
import plutoproject.capability.flag.api.stringFlag
import plutoproject.capability.redis.api.RedisConnection
import java.util.UUID

/**
 * 针对真实 Redis 的集成测试，验证 Lettuce 读写、hash 布局和 SCAN 列表行为。
 */
/**
 * 直接包一层 Lettuce 客户端，避免依赖 redis 模块的 internal 实现。
 */
@OptIn(ExperimentalLettuceCoroutinesApi::class)
internal class TestRedisConnection(uri: String) : RedisConnection {
    override val client: RedisClient = RedisClient.create(uri)
    override val connection: StatefulRedisConnection<String, String> = client.connect()
    override val coroutines: RedisCoroutinesCommands<String, String> = connection.coroutines()

    fun close() {
        connection.close()
        client.shutdown()
    }
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FlagRegistryIntegrationTest {
    private val redis = GenericContainer("redis:7-alpine").apply { withExposedPorts(6379) }
    private lateinit var connection: TestRedisConnection
    private lateinit var raw: RedisCommands<String, String>

    @BeforeAll
    fun setUp() {
        redis.start()
        val uri = "redis://${redis.host}:${redis.getMappedPort(6379)}"
        connection = TestRedisConnection(uri)
        raw = RedisClient.create(uri).connect().sync()
    }

    @AfterAll
    fun tearDown() {
        connection.close()
        redis.stop()
    }

    @BeforeEach
    fun flush() {
        raw.flushdb()
    }

    private fun newRegistry(serverId: String = "survival-1"): FlagRegistryImpl = FlagRegistryImpl(
        store = LettuceFlagStore(connection),
        serverIdentifier = serverId,
        cache = FlagCache(5.seconds),
        backgroundScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
        allowWorldScope = true,
    ).also { runBlocking { it.preload() } }

    @Test
    fun `set writes the serialized value into the real redis hash`() = runBlocking {
        val registry = newRegistry()
        val key = booleanFlag("it.enabled", default = false)
        registry.register(key)

        registry.set(key, true, Scope.SERVER)

        assertEquals("true", raw.hget(serverScopeHash("survival-1"), "it.enabled"))
    }

    @Test
    fun `clear removes the field and drops the emptied hash`() = runBlocking {
        val registry = newRegistry()
        val key = stringFlag("it.name", default = "")
        registry.register(key)
        registry.set(key, "alpha", Scope.SERVER)

        val removed = registry.clear(key, Scope.SERVER)

        assertTrue(removed)
        assertNull(raw.hget(serverScopeHash("survival-1"), "it.name"))
        assertEquals(0L, raw.exists(serverScopeHash("survival-1")))
    }

    @Test
    fun `reads fall back from player to server to global with real redis`() = runBlocking {
        val registry = newRegistry()
        val key = intFlag("it.timeout", default = 30)
        registry.register(key)
        val uuid = UUID.randomUUID()
        val globalHash = globalScopeHash()
        val serverHash = serverScopeHash("survival-1")

        registry.set(key, 60, Scope.GLOBAL)
        registry.set(key, 90, Scope.SERVER)

        assertEquals(90, registry.get(key, Scope.player(uuid)))
        assertEquals(90, registry.getDirect(key, Scope.player(uuid)))

        // 外部直改 Redis 不经过本地缓存，get 看到的是旧值，getDirect 立刻可见。
        raw.hdel(serverHash, "it.timeout")
        assertEquals(90, registry.get(key, Scope.player(uuid)))
        assertEquals(60, registry.getDirect(key, Scope.player(uuid)))
        assertEquals(60, registry.getDirect(key))

        raw.del(globalHash)
        assertEquals(30, registry.getDirect(key, Scope.player(uuid)))
    }

    @Test
    fun `getDirect sees external writes that the cache has not picked up`() = runBlocking {
        val registry = newRegistry()
        val key = stringFlag("it.external", default = "")
        registry.register(key)
        raw.hset(globalScopeHash(), "it.external", "from-elsewhere")

        // 缓存未命中时按缺席处理，直接读才可见外部写入。
        assertEquals("", registry.get(key))
        assertEquals("from-elsewhere", registry.getDirect(key))
    }

    @Test
    fun `invalid stored values are skipped instead of failing the read`() = runBlocking {
        val registry = newRegistry()
        val key = intFlag("it.broken", default = 7)
        registry.register(key)
        raw.hset(serverScopeHash("survival-1"), "it.broken", "not-a-number")

        assertEquals(7, registry.getDirect(key))
    }

    @Test
    fun `raw listing scans the real hashes and filters by scope kind`() = runBlocking {
        val registry = newRegistry()
        val serverKey = intFlag("it.server_only", default = 0)
        val playerKey = intFlag("it.player_only", default = 0)
        val worldKey = intFlag("it.world_only", default = 0)
        registry.register(serverKey)
        registry.register(playerKey)
        registry.register(worldKey)
        registry.set(serverKey, 1, Scope.SERVER)
        registry.set(playerKey, 2, Scope.player(UUID.randomUUID()))
        registry.set(worldKey, 3, Scope.world("world_nether"))

        val players = registry.list(ListFilter.Player(null)) as FlagListing.RawEntries
        assertEquals(1, players.entries.size)
        assertEquals("it.player_only", players.entries.single().key.name)
        assertEquals("2", players.entries.single().value)

        val worlds = registry.list(ListFilter.World(null)) as FlagListing.RawEntries
        assertEquals(1, worlds.entries.size)
        assertEquals("it.world_only", worlds.entries.single().key.name)
        assertEquals("3", worlds.entries.single().value)
    }
}
