package ink.pmc.framework.provider

import com.maxmind.geoip2.DatabaseReader
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import ink.pmc.framework.FrameworkConfig
import ink.pmc.framework.frameworkDataFolder
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.io.File

class ProviderImpl : Provider, KoinComponent {
    private val config by lazy { get<FrameworkConfig>().provider }
    override val mongoClient: MongoClient
    override val defaultMongoDatabase: MongoDatabase
    override val geoIpDatabase: DatabaseReader

    init {
        val mongoConfig = config.mongo
        mongoClient =
            MongoClient.create("mongodb://${mongoConfig.username}:${mongoConfig.password}@${mongoConfig.host}:${mongoConfig.port}/${mongoConfig.database}?uuidRepresentation=standard&connectTimeoutMS=0&timeoutMS=0")
        defaultMongoDatabase = mongoClient.getDatabase(mongoConfig.database)
        val geoIpConfig = config.geoIp
        val dbFile = File(frameworkDataFolder, geoIpConfig.database)
        check(dbFile.exists()) { "GeoIP database file not found" }
        geoIpDatabase = DatabaseReader.Builder(dbFile).build()
    }

    override fun close() {
        mongoClient.close()
        geoIpDatabase.close()
    }
}