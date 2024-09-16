package ink.pmc.member

import ink.pmc.member.storage.BedrockAccountBean
import ink.pmc.member.storage.DataContainerBean
import ink.pmc.member.storage.MemberBean
import ink.pmc.member.storage.StatusBean
import org.javers.core.JaversBuilder
import java.util.logging.Logger

var disabled = true
lateinit var serverLogger: Logger
lateinit var memberService: BaseMemberServiceImpl
val javers = JaversBuilder.javers()
    .registerEntity(BedrockAccountBean::class.java)
    .registerEntity(DataContainerBean::class.java)
    .registerEntity(MemberBean::class.java)
    .registerEntity(StatusBean::class.java)
    .registerValueTypeAdapter(bsonDocumentAdapter)
    .build()!!

fun safeDisable() {
    memberService.loadedMembers.synchronous().invalidateAll()
    memberService.loadedMembers.synchronous().cleanUp()
}