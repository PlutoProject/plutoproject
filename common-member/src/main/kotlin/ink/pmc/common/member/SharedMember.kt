package ink.pmc.common.member

import com.electronwill.nightconfig.core.file.FileConfig
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.MemberAPI
import ink.pmc.common.member.api.MemberManager
import ink.pmc.common.member.impl.MemberAPIImpl
import ink.pmc.common.member.impl.MemberManagerImpl
import org.bson.Document
import org.bson.UuidRepresentation
import org.mongojack.JacksonMongoCollection
import org.mongojack.ObjectMapperConfigurer
import java.io.File

var disabled = true
lateinit var memberManager: MemberManager
lateinit var dataDir: File
lateinit var configFile: File
lateinit var config: FileConfig
lateinit var memberCollection: JacksonMongoCollection<Member>
lateinit var punishmentIndexCollection: MongoCollection<Document>
lateinit var commentIndexCollection: MongoCollection<Document>
lateinit var mongoClient: MongoClient

fun createDataDir() {
    if (!dataDir.exists()) {
        dataDir.mkdirs()
    }
}

fun loadConfig(file: File) {
    config = FileConfig.builder(file).sync().build()
    config.load()
}

fun isEnabled(): Boolean {
    return config.get("enabled")
}

fun connectDatabase() {
    MemberAPIImpl.internalMemberManager // 显式调用，来让单例类被加载

    val hostname = config.get<String>("hostname")
    val port = config.get<String>("port")
    val username = config.get<String>("username")
    val password = config.get<String>("password")
    val database = config.get<String>("database")

    mongoClient =
        MongoClients.create("mongodb://$username:$password@$hostname:$port/$database?uuidRepresentation=standard")
    val db = mongoClient.getDatabase(database)
    ObjectMapperConfigurer.configureObjectMapper(MemberAPI.instance.objectMapper)
    val member = JacksonMongoCollection.builder()
        .withObjectMapper(MemberAPI.instance.objectMapper)
        .build(db, "member", Member::class.java, UuidRepresentation.STANDARD)
    val indexPunishment = db.getCollection("member_punishment_index")
    val indexComment = db.getCollection("member_comment_index")

    memberCollection = member
    punishmentIndexCollection = indexPunishment
    commentIndexCollection = indexComment
}

fun initMemberManager() {
    loadConfig(configFile)
    connectDatabase()
    memberManager = MemberManagerImpl(memberCollection, punishmentIndexCollection, commentIndexCollection)
    initAPI()
}

fun initAPI() {
    MemberAPIImpl.internalMemberManager = memberManager
}