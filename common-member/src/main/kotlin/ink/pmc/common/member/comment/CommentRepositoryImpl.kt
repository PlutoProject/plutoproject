package ink.pmc.common.member.comment

import ink.pmc.common.member.api.comment.Comment
import ink.pmc.common.member.api.comment.CommentRepository

class CommentRepositoryImpl : CommentRepository {

    override fun list(): Collection<Comment> {
        TODO("Not yet implemented")
    }

    override fun comment(creator: Long, content: String): Comment {
        TODO("Not yet implemented")
    }

    override fun set(creator: Long, content: String) {
        TODO("Not yet implemented")
    }

    override fun modify(id: Long, new: String): Comment {
        TODO("Not yet implemented")
    }

    override fun lookup(id: Long): Comment? {
        TODO("Not yet implemented")
    }

    override fun get(id: Long): Comment? {
        TODO("Not yet implemented")
    }

    override fun uncomment(id: Long) {
        TODO("Not yet implemented")
    }

}