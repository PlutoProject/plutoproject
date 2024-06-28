package ink.pmc.provider

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import java.io.Closeable

object ProviderService : IProviderService by IProviderService.instance

interface IProviderService : Closeable {

    companion object {
        lateinit var instance: IProviderService
    }

    val mongoClient: MongoClient
    val defaultMongoDatabase: MongoDatabase
}