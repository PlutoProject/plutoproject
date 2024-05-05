package ink.pmc.common.member.comment

import ink.pmc.common.member.AbstractMember
import ink.pmc.common.member.AbstractMemberService
import ink.pmc.common.member.api.comment.Comment
import ink.pmc.common.member.storage.CommentStorage
import ink.pmc.common.utils.concurrent.submitAsyncIO
import org.bson.types.ObjectId

class CommentRepositoryImpl(private val service: AbstractMemberService, private val member: AbstractMember) :
    AbstractCommentRepository() {

    override lateinit var comments: MutableCollection<Comment>

    init {
        submitAsyncIO {
            comments = member.storage.comments.map {
                CommentImpl(member, service.lookupCommentStorage(it)!!)
            }.toMutableList()
        }
    }

    override fun comment(creator: Long, content: String): Comment {
        val id = service.currentStatus.nextComment()

        val storage = CommentStorage(
            ObjectId(),
            id,
            System.currentTimeMillis(),
            creator,
            content,
            false
        )

        service.currentStatus.increaseComment()
        member.storage.comments.add(id)

        return CommentImpl(member, storage)
    }

    override fun set(creator: Long, content: String) {
        comment(creator, content)
    }

    override fun modify(id: Long, new: String): Comment? {
        if (!exist(id)) {
            return null
        }

        val comment = comments.first { it.id == id } as CommentImpl

        comment.content = new
        comment.isModified = true

        return comment
    }

    override fun lookup(id: Long): Comment? {
        if (!exist(id)) {
            return null
        }

        val comment = comments.first { it.id == id } as AbstractComment

        return comment
    }

    override fun get(id: Long): Comment? {
        return lookup(id)
    }

    override fun exist(id: Long): Boolean {
        return comments.firstOrNull { it.id == id } != null
    }

    override fun uncomment(id: Long) {
        if (!exist(id)) {
            return
        }

        val comment = comments.first { it.id == id } as CommentImpl
        dirtyComments.add(comment.storage)
        comments.removeIf { it.id == id }
    }

}