package ink.pmc.common.refactor.member.api.comment

@Suppress("UNUSED")
interface CommentRepository {

    fun list(): Collection<Comment>

    fun comment(creator: Long, content: String): Comment

    operator fun set(creator: Long, content: String)

    fun modify(id: Long, new: String): Comment

    fun lookup(id: Long): Comment?

    operator fun get(id: Long): Comment?

    fun uncomment(id: Long)

}