package ink.pmc.common.member.api

import com.fasterxml.jackson.databind.ObjectMapper
import ink.pmc.common.member.api.dsl.MemberDSL

interface MemberAPI {

    companion object {
        private var _instance: MemberAPI? = null
            set(value) {
                if (field != null) {
                    return
                }

                field = value
            }

        var instance: MemberAPI
            get() {
                if (_instance == null) {
                    throw RuntimeException("MemberAPI not initialized")
                }

                return _instance!!
            }
            set(value) {
                if (_instance != null) {
                    return
                }

                _instance = value
            }
    }

    val memberManager: MemberManager
    val objectMapper: ObjectMapper

    fun createMember(block: MemberDSL.() -> Unit): Member

}