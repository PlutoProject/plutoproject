package ink.pmc.framework

data class FrameworkConfig(
    val provider: ProviderConfig,
    val rpc: RpcConfig,
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