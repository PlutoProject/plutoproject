package ink.pmc.common.member.api.comment

import ink.pmc.common.member.api.Member
import java.time.Instant

@Suppress("UNUSED")
interface Comment {

    val id: Long
    val createdAt: Instant
    val creator: Member
    val content: String
    val isModified: Boolean

}