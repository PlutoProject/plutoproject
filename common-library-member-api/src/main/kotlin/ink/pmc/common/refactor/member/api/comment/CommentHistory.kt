package ink.pmc.common.refactor.member.api.comment

import ink.pmc.common.refactor.member.api.data.History
import java.time.LocalDateTime

@Suppress("UNUSED")
data class CommentHistory(override var lastModifiedAt: LocalDateTime?) : History<Comment>()