package ink.pmc.common.member

import com.electronwill.nightconfig.core.file.FileConfig
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import ink.pmc.common.member.api.IMemberService
import java.io.File
import java.util.*
import java.util.logging.Logger

var disabled = true
lateinit var serverLogger: Logger
lateinit var memberService: AbstractMemberService
lateinit var dataDir: File
lateinit var configFile: File
lateinit var config: FileConfig
lateinit var mongoClient: MongoClient
lateinit var database: MongoDatabase

/*
* Floodgate 依赖问题，会与新版 Gson 冲突，
* 见 https://github.com/GeyserMC/Floodgate/issues/495。
* 在他们解决这个问题之前，先使用反射来判断是否为 Floodgate 玩家。
* */
private val floodgateApiClass = Class.forName("org.geysermc.floodgate.api.FloodgateApi")
private val floodgateApi = run {
    val method = floodgateApiClass.getDeclaredMethod("getInstance")
    method.invoke(null)
}
private val isFloodgatePlayer = floodgateApiClass.getDeclaredMethod("isFloodgatePlayer", UUID::class.java)

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

fun isFloodgateSession(uuid: UUID): Boolean {
    return isFloodgatePlayer.invoke(floodgateApi, uuid) as Boolean
}

fun safeDisable() {
    memberService.loadedMembers.invalidateAll()
    memberService.loadedMembers.cleanUp()
    mongoClient.close()
}