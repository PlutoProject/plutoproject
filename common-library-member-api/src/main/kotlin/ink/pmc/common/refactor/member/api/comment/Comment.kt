package ink.pmc.common.refactor.member.api.comment

import java.lang.reflect.Member
import java.time.LocalDateTime

@Suppress("UNUSED")
interface Comment {

    val id: Long
    val createdAt: LocalDateTime
    val creator: Member
    val content: String
    val isModified: Boolean

}