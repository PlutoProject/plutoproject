package ink.pmc.common.member.api

import java.util.*

interface Comment {

    val id: Long
    val creator: UUID
    val owner: UUID
    val date: Date
    var content: String
    var isModified: Boolean

}