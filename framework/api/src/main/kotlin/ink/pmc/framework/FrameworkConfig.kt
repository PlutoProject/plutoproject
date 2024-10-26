package ink.pmc.framework

data class FrameworkConfig(
    val provider: ProviderConfig,
    val rpc: RpcConfig,
    val worldAliases: Map<String, String>
)

data class ProviderConfig(
    val mongo: MongoDbConfig
)

data class MongoDbConfig(
    val host: String,
    val port: Int,
    val database: String,
    val username: String,
    val password: String
)

data class RpcConfig(
    val host: String,
    val port: Int
)