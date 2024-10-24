package ink.pmc.framework.provider

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import ink.pmc.framework.FrameworkConfig
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class ProviderImpl : Provider, KoinComponent {
    private val config by lazy { get<FrameworkConfig>().provider }
    override val mongoClient: MongoClient
    override val defaultMongoDatabase: MongoDatabase

    init {
        val (client, db) = connectMongo()
        mongoClient = client
        defaultMongoDatabase = db
    }

    private fun connectMongo(): Pair<MongoClient, MongoDatabase> {
        val mongo = config.mongo
        val client =
            MongoClient.create("mongodb://${mongo.username}:${mongo.password}@${mongo.host}:${mongo.port}/${mongo.database}?uuidRepresentation=standard")
        val db = client.getDatabase(mongo.database)
        return client to db
    }

    override fun close() {
        mongoClient.close()
    }
}