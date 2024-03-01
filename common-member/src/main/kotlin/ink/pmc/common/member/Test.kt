package ink.pmc.common.member

import com.mongodb.client.MongoClients
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.MemberAPI
import org.bson.UuidRepresentation
import org.mongojack.JacksonMongoCollection
import org.mongojack.ObjectMapperConfigurer
import java.util.*

fun main() {

    MemberAPIImpl.internalMemberManager

    val mongoClient = MongoClients.create("mongodb://root:mongodb123@localhost:27017/admin?uuidRepresentation=standard")
    val database = mongoClient.getDatabase("pluto")
    ObjectMapperConfigurer.configureObjectMapper(MemberAPI.instance.objectMapper)
    val collection = JacksonMongoCollection.builder()
        .withObjectMapper(MemberAPI.instance.objectMapper)
        .build(database, "member", Member::class.java, UuidRepresentation.STANDARD)
    val indexPunishment = database.getCollection("member_punishment_index")
    val indexComment = database.getCollection("member_comment_index")

    val memberManager = MemberManagerImpl(
        collection,
        indexPunishment,
        indexComment
    )

    MemberAPIImpl.internalMemberManager = memberManager

    /*    val member = memberManager.createAndRegister {
            uuid = UUID.randomUUID()
            name = "nostalfinals"
        }*/
    val member = memberManager.get(UUID.fromString("c354a025-e615-4439-8042-ad71883eb67c"))!!

    println(member.punishments)

}