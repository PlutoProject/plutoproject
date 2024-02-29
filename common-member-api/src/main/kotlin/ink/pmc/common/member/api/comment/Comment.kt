package ink.pmc.common.member.api.comment

import java.util.*

interface Comment {

    val id: Long
    val owner: UUID
    val creator: UUID
    val createTime: Date
    var isModified: Boolean
    var content: String?

}