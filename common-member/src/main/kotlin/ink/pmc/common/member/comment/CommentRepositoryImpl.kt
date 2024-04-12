package ink.pmc.common.member.comment

import ink.pmc.common.member.AbstractMember
import ink.pmc.common.member.AbstractMemberService
import ink.pmc.common.member.api.comment.Comment
import ink.pmc.common.member.storage.CommentStorage
import org.bson.types.ObjectId

class CommentRepositoryImpl(private val service: AbstractMemberService, private val member: AbstractMember) :
    AbstractCommentRepository() {

    override val comments: Collection<Comment>
        get() {
            val list = mutableListOf<Comment>()

            member.storage.comments.forEach {
                list.add(CommentImpl(service, service.cachedComment(it)!!))
            }

            return list
        }

    override fun comment(creator: Long, content: String): Comment {
        val id = service.cachedStatus().nextComment()

        val storage = CommentStorage(
            ObjectId(),
            id,
            System.currentTimeMillis(),
            creator,
            content,
            false
        )

        service.cachedStatus().increaseComment()
        service.cacheComment(id, storage)
        member.storage.comments.add(id)

        return CommentImpl(service, storage)
    }

    override fun set(creator: Long, content: String) {
        comment(creator, content)
    }

    override fun modify(id: Long, new: String): Comment? {
        if (!exist(id)) {
            return null
        }

        val storage = service.cachedComment(id)!!

        storage.content = new
        storage.isModified = true
        service.cacheComment(storage.id, storage)

        return CommentImpl(service, storage)
    }

    override fun lookup(id: Long): Comment? {
        if (!exist(id)) {
            return null
        }

        return CommentImpl(service, service.cachedComment(id)!!)
    }

    override fun get(id: Long): Comment? {
        return lookup(id)
    }

    override fun exist(id: Long): Boolean {
        return service.cachedComment(id) != null
    }

    override fun uncomment(id: Long) {
        if (!exist(id)) {
            return
        }

        service.cachedComment(id)!!.removal = true
    }

}