package ink.pmc.common.member

import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.MemberAPI
import ink.pmc.common.member.api.MemberManager
import ink.pmc.common.member.api.dsl.MemberDSL

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

    override fun createMember(block: MemberDSL.() -> Unit): Member {
        val dsl = MemberDSL()
        dsl.block()

        if (dsl.uuid == null || dsl.name == null || dsl.joinTime == null) {
            throw RuntimeException("Required information missed")
        }

        return MemberImpl(dsl.uuid!!, dsl.name!!, dsl.joinTime!!)
    }

}