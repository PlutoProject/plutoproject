package ink.pmc.common.member.api.comment

import ink.pmc.common.member.api.Member

@Suppress("UNUSED")
interface CommentContainer {

    val comments: Collection<Comment>

    fun comment(creator: Long, content: String): Comment

    operator fun set(creator: Long, content: String)

    fun comment(creator: Member, comment: String): Comment

    operator fun set(creator: Member, content: String)

    fun modify(id: Long, new: String): Comment?

    operator fun get(id: Long): Comment?

    fun exist(id: Long): Boolean

    fun uncomment(id: Long)

}