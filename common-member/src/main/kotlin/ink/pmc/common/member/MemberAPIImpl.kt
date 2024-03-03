package ink.pmc.common.member

import com.fasterxml.jackson.databind.ObjectMapper
import ink.pmc.common.member.api.*
import ink.pmc.common.member.api.dsl.MemberDSL
import ink.pmc.common.member.api.punishment.Punishment

object MemberAPIImpl : MemberAPI {

    init {
        MemberAPI.instance = MemberAPIImpl
    }

    var internalMemberManager: MemberManager? = null
        set(value) {
            if (field != null) {
                return
            }

            field = value
        }
    override val memberManager: MemberManager
        get() {
            if (internalMemberManager == null) {
                throw RuntimeException("MemberManager not initialized")
            }

            return internalMemberManager!!
        }
    override val objectMapper: ObjectMapper = ObjectMapper().apply {
        addMixIn(Member::class.java, MemberMixin::class.java)
        addMixIn(Punishment::class.java, PunishmentMixin::class.java)
        addMixIn(Comment::class.java, CommentMixin::class.java)
        addMixIn(MemberData::class.java, MemberDataMixin::class.java)
    }

    override fun createMember(block: MemberDSL.() -> Unit): Member {
        val dsl = MemberDSL()
        dsl.block()

        if (dsl.uuid == null || dsl.name == null) {
            throw RuntimeException("Required information missed")
        }

        val member = MemberImpl(dsl.uuid!!, dsl.name!!)

        if (dsl.joinTime != null) {
            member.joinTime = dsl.joinTime!!
        }

        return member
    }

}