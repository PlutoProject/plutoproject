package ink.pmc.common.member.comment

import ink.pmc.common.member.AbstractMember
import ink.pmc.common.member.AbstractMemberService
import ink.pmc.common.member.COMMENTS_KEY
import ink.pmc.common.member.api.comment.Comment
import ink.pmc.common.member.storage.CommentStorage

class CommentContainerImpl(private val service: AbstractMemberService, private val member: AbstractMember) :
    AbstractCommentContainer() {
    override val comments: MutableCollection<AbstractComment>
        get() = lookup()

    private fun update(list: MutableCollection<AbstractComment>) {
        member.dataContainer[COMMENTS_KEY] = list.map { it.storage }
    }

    private fun lookup(): MutableCollection<AbstractComment> {
        return member.dataContainer.getCollection(COMMENTS_KEY, CommentStorage::class.java)!!.map {
            CommentImpl(it)
        }.toMutableList()
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

        val comment = CommentImpl(storage)
        val updated = lookup().apply { add(comment) }
        update(updated)
        service.currentStatus.increaseComment()

        return comment
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

        val updated = lookup().toMutableList().apply {
            val index = indexOfFirst { it.id == id }
            add(index, comment)
        }
        update(updated)

        return comment
    }

    override fun get(id: Long): Comment? {
        if (!exist(id)) {
            return null
        }

        val comment = comments.first { it.id == id }
        return comment
    }

    override fun exist(id: Long): Boolean {
        return comments.firstOrNull { it.id == id } != null
    }

    override fun uncomment(id: Long) {
        if (!exist(id)) {
            return
        }

        val updated = lookup().apply { removeIf { it.id == id } }
        update(updated)
    }

}