package ink.pmc.framework.provider

import com.maxmind.geoip2.DatabaseReader
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import ink.pmc.framework.inject.inlinedGet
import java.io.Closeable

inline fun <reified T : Any> Provider.getCollection(name: String): MongoCollection<T> {
    return defaultMongoDatabase.getCollection(name)
}

interface Provider : Closeable {
    companion object : Provider by inlinedGet()

    val mongoClient: MongoClient
    val defaultMongoDatabase: MongoDatabase
    val geoIpDatabase: DatabaseReader
}