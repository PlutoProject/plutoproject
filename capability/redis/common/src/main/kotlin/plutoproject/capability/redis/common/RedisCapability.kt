package plutoproject.capability.redis.common

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.PropertySource
import com.sksamuel.hoplite.hocon.HoconParser
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.coroutines
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import org.koin.dsl.module
import org.koin.dsl.onClose
import plutoproject.capability.redis.api.RedisConnection
import plutoproject.kernel.api.ModuleContext
import plutoproject.kernel.api.RuntimeModule
import plutoproject.kernel.api.exportServiceFromKoin
import plutoproject.kernel.api.loadKoinModuleDefinitions

@OptIn(ExperimentalHoplite::class)
class RedisCapability : RuntimeModule {
    override suspend fun onLoad(context: ModuleContext) {
        context.dataFolder.toFile().mkdirs()
        val configFile = context.saveResource("config.conf")
        val config = ConfigLoaderBuilder.empty()
            .withClassLoader(RedisCapability::class.java.classLoader)
            .withExplicitSealedTypes()
            .addDefaults()
            .addParser("conf", HoconParser())
            .addPropertySource(PropertySource.file(configFile.toFile()))
            .build()
            .loadConfigOrThrow<RedisConfig>()
        context.loadKoinModuleDefinitions(module {
            single { DefaultRedisConnection(config) }.onClose { it?.close() }
            single<RedisConnection> { get<DefaultRedisConnection>() }
        })
        context.services.exportServiceFromKoin<RedisConnection>()
    }
}

@OptIn(ExperimentalLettuceCoroutinesApi::class)
internal class DefaultRedisConnection(config: RedisConfig) : RedisConnection, AutoCloseable {
    override val client: RedisClient = RedisClient.create(config.redisUri)
    override val connection: StatefulRedisConnection<String, String> = client.connect()
    override val coroutines: RedisCoroutinesCommands<String, String> = connection.coroutines()

    override fun close() {
        connection.close()
        client.shutdown()
    }
}

internal data class RedisConfig(
    val redisUri: String = "redis://localhost:6379",
)
