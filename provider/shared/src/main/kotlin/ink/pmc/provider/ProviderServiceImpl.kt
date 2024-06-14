package ink.pmc.provider

import com.electronwill.nightconfig.core.file.FileConfig
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase

class ProviderServiceImpl(private val config: FileConfig) : IProviderService {

    override val mongoClient: MongoClient
    override val defaultMongoDatabase: MongoDatabase

    init {
        val (client, db) = connectMongo()
        mongoClient = client
        defaultMongoDatabase = db
    }

    private fun connectMongo(): Pair<MongoClient, MongoDatabase> {
        val host = config.get<String>("host")
        val port = config.get<String>("port")
        val database = config.get<String>("database")
        val username = config.get<String>("username")
        val password = config.get<String>("password")

        val client = MongoClient.create("mongodb://$username:$password@$host:$port/$database?uuidRepresentation=standard")
        val db = mongoClient.getDatabase(database)

        return client to db
    }

}