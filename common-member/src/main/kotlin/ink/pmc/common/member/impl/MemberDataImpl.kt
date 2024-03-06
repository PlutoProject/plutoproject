package ink.pmc.common.member.impl

import com.fasterxml.jackson.annotation.JsonCreator
import ink.pmc.common.member.api.MemberData

class MemberDataImpl @JsonCreator constructor() : MemberData {

    override val data: MutableMap<String, Any> = mutableMapOf()

}