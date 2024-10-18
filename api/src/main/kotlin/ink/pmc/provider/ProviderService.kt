package ink.pmc.provider

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import ink.pmc.utils.inject.inlinedGet
import java.io.Closeable

interface ProviderService : Closeable {

    companion object : ProviderService by inlinedGet()

    val mongoClient: MongoClient
    val defaultMongoDatabase: MongoDatabase

}