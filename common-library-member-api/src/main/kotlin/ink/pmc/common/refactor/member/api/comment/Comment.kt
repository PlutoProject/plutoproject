package ink.pmc.common.refactor.member.api.comment

import java.time.LocalDateTime

@Suppress("UNUSED")
interface Comment {

    val id: Long
    val createdAt: LocalDateTime
    val creator: Long
    val content: String
    val isModified: Boolean

}