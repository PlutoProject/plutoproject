package plutoproject.capability.redis.api

import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands

interface RedisConnection {
    val client: RedisClient
    val connection: StatefulRedisConnection<String, String>
    val coroutines: RedisCoroutinesCommands<String, String>
}
