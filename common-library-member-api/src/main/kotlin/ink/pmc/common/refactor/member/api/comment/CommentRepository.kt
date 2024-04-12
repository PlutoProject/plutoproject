package ink.pmc.common.refactor.member.api.comment

@Suppress("UNUSED")
interface CommentRepository {

    fun list(): Collection<Comment>

    fun comment(blockL: CommentDsl.() -> Unit): Comment

    fun modify(id: Long, new: String): Comment

    fun uncomment(id: Long)

}