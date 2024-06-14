package ink.pmc.provider

import com.electronwill.nightconfig.core.Config
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
        val mongo = config.get<Config>("mongo")

        val host = mongo.get<String>("host")
        val port = mongo.get<Int>("port")
        val database = mongo.get<String>("database")
        val username = mongo.get<String>("username")
        val password = mongo.get<String>("password")

        val client = MongoClient.create("mongodb://$username:$password@$host:$port/$database?uuidRepresentation=standard")
        val db = client.getDatabase(database)

        return client to db
    }

    override fun close() {
        mongoClient.close()
    }

}