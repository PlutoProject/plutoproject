package plutoproject.capability.flag.common

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.PropertySource
import com.sksamuel.hoplite.hocon.HoconParser
import plutoproject.capability.flag.api.FlagRegistry
import plutoproject.capability.redis.api.RedisConnection
import plutoproject.capability.serveridentifier.api.ServerIdentifier
import plutoproject.kernel.api.ModuleContext
import plutoproject.kernel.api.RuntimeModule
import plutoproject.kernel.api.exportService
import plutoproject.kernel.api.importServiceToKoin
import plutoproject.kernel.api.koinGet
import kotlin.time.Duration.Companion.seconds

/**
 * 共享的 flag capability 逻辑。[allowWorldScope] 在没有世界上下文的平台
 * （Velocity）上必须为 false。
 */
@OptIn(ExperimentalHoplite::class)
class FlagCapability(
    private val allowWorldScope: Boolean = true,
) : RuntimeModule {
    override suspend fun onLoad(context: ModuleContext) {
        context.dataFolder.toFile().mkdirs()
        val configFile = context.saveResource("config.conf")
        val config = ConfigLoaderBuilder.empty()
            .withClassLoader(FlagCapability::class.java.classLoader)
            .withExplicitSealedTypes()
            .addDefaults()
            .addParser("conf", HoconParser())
            .addPropertySource(PropertySource.file(configFile.toFile()))
            .build()
            .loadConfigOrThrow<FlagConfig>()
        context.importServiceToKoin<RedisConnection>()
        context.importServiceToKoin<ServerIdentifier>()
        val serverIdentifier = context.koinGet<ServerIdentifier>().identifierOrThrow()
        require(serverIdentifier != RESERVED_GLOBAL_IDENTIFIER) {
            "Server identifier \"$RESERVED_GLOBAL_IDENTIFIER\" is reserved by the flag system"
        }
        val ttl = config.cacheTtlSeconds.seconds
        val registry = FlagRegistryImpl(
            store = LettuceFlagStore(context.koinGet<RedisConnection>()),
            serverIdentifier = serverIdentifier,
            cache = FlagCache(ttl),
            backgroundScope = context.coroutineScope,
            allowWorldScope = allowWorldScope,
        )
        registry.preload()
        registry.startRefreshLoop(context.coroutineScope, ttl)
        context.services.exportService<FlagRegistry>(registry)
    }
}

internal data class FlagConfig(
    val cacheTtlSeconds: Long = 5,
)
