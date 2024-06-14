package ink.pmc.provider

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase

object ProviderService : IProviderService by IProviderService.instance

interface IProviderService {

    companion object {
        lateinit var instance: IProviderService
    }

    val mongoClient: MongoClient
    val defaultMongoDatabase: MongoDatabase

}