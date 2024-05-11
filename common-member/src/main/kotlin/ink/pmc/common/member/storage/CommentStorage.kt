package ink.pmc.common.member.storage

data class CommentStorage(
    val id: Long,
    val createdAt: Long,
    val creator: Long,
    var content: String,
    var isModified: Boolean
)