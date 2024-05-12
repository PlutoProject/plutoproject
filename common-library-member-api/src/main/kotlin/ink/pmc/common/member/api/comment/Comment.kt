package ink.pmc.common.member.api.comment

import ink.pmc.common.member.api.Member
import java.time.Instant

@Suppress("UNUSED")
interface Comment {

    val id: Long
    val createdAt: Instant
    val content: String
    val isModified: Boolean

    suspend fun creator(): Member?

}