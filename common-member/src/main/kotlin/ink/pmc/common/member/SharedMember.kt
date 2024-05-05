package ink.pmc.common.member

import com.electronwill.nightconfig.core.file.FileConfig
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import ink.pmc.common.member.api.IMemberService
import java.io.File
import java.util.logging.Logger

var disabled = true
lateinit var serverLogger: Logger
lateinit var memberService: AbstractMemberService
lateinit var dataDir: File
lateinit var configFile: File
lateinit var config: FileConfig
lateinit var mongoClient: MongoClient
lateinit var database: MongoDatabase

fun createDataDir() {
    if (!dataDir.exists()) {
        dataDir.mkdirs()
    }
}

fun loadConfig(file: File) {
    config = FileConfig.builder(file).sync().build()
    config.load()
}

fun connectDatabase() {
    val hostname = config.get<String>("hostname")
    val port = config.get<String>("port")
    val username = config.get<String>("username")
    val password = config.get<String>("password")
    val db = config.get<String>("database")

    mongoClient = MongoClient.create("mongodb://$username:$password@$hostname:$port/$db?uuidRepresentation=standard")
    database = mongoClient.getDatabase(db)
}

fun initMemberService() {
    loadConfig(configFile)
    connectDatabase()
    memberService = MemberServiceImpl(database)
    IMemberService.instance = memberService
}

fun safeDisable() {
    memberService.loadedMembers.synchronous().invalidateAll()
    memberService.loadedMembers.synchronous().cleanUp()
    mongoClient.close()
}