package ink.pmc.provider

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import ink.pmc.framework.utils.inject.inlinedGet
import java.io.Closeable

interface Provider : Closeable {

    companion object : Provider by inlinedGet()

    val mongoClient: MongoClient
    val defaultMongoDatabase: MongoDatabase

}