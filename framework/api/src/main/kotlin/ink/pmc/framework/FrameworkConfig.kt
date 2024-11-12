package ink.pmc.framework

import kotlin.time.Duration

data class FrameworkConfig(
    val preload: Boolean,
    val provider: ProviderConfig,
    val rpc: RpcConfig,
    val bridge: BridgeConfig,
    val worldAliases: Map<String, String>
)

data class ProviderConfig(
    val mongo: MongoDb,
    val geoIp: GeoIp
)

data class MongoDb(
    val host: String,
    val port: Int,
    val database: String,
    val username: String,
    val password: String
)

data class GeoIp(
    val database: String
)

data class RpcConfig(
    val host: String,
    val port: Int
)

data class BridgeConfig(
    val debug: Boolean,
    val operationTimeout: Duration,
    val id: String,
    val group: String?
)