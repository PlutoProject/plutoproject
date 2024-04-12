package ink.pmc.common.member.api.comment

import java.lang.reflect.Member
import java.time.Instant
import java.time.LocalDateTime

@Suppress("UNUSED")
interface Comment {

    val id: Long
    val createdAt: Instant
    val creator: Member
    val content: String
    val isModified: Boolean

}