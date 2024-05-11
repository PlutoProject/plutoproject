package ink.pmc.common.member.comment

import ink.pmc.common.member.AbstractMember
import ink.pmc.common.member.AbstractMemberService
import ink.pmc.common.member.COMMENTS_KEY
import ink.pmc.common.member.api.comment.Comment
import ink.pmc.common.member.memberService
import ink.pmc.common.member.storage.CommentStorage
import ink.pmc.common.utils.concurrent.submitAsyncIO

class CommentContainerImpl(private val service: AbstractMemberService, private val member: AbstractMember) :
    AbstractCommentContainer() {

    private val commentStorages: MutableCollection<CommentStorage> = mutableListOf()
    override lateinit var comments: MutableCollection<Comment>

    private fun loadComments() {
        commentStorages.clear()

        if (!member.dataContainer.contains(COMMENTS_KEY)) {
            return
        }

        val storages = member.dataContainer.getCollection(COMMENTS_KEY, CommentStorage::class.java)!!.toMutableList()
        commentStorages.addAll(storages)
    }

    init {
        submitAsyncIO {
            loadComments()
            comments = commentStorages.map {
                CommentImpl(it, memberService.lookup(it.creator)!!)
            }.toMutableList()
        }
    }

    override fun comment(creator: Long, content: String): Comment {
        val id = service.currentStatus.nextComment()

        val storage = CommentStorage(
            id,
            System.currentTimeMillis(),
            creator,
            content,
            false
        )

        service.currentStatus.increaseComment()
        return CommentImpl(storage, member)
    }

    override fun set(creator: Long, content: String) {
        comment(creator, content)
    }

    override fun reload() {
        loadComments()
    }

    override fun save() {
        member.dataContainer.remove(COMMENTS_KEY)

        val storages = comments.map {
            CommentStorage(
                it.id,
                it.createdAt.toEpochMilli(),
                it.creator.uid,
                it.content,
                it.isModified
            )
        }

        member.dataContainer[COMMENTS_KEY] = storages
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

    override fun get(id: Long): Comment? {
        if (!exist(id)) {
            return null
        }

        val comment = comments.first { it.id == id } as AbstractComment
        return comment
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