package ink.pmc.common.member.comment

import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.comment.Comment
import ink.pmc.common.member.api.comment.CommentContainer
import ink.pmc.common.member.storage.CommentStorage

abstract class AbstractCommentContainer : CommentContainer {

    val dirtyComments = mutableListOf<CommentStorage>()

    override fun comment(creator: Member, comment: String): Comment {
        return comment(creator.uid, comment)
    }

    override fun set(creator: Member, content: String) {
        set(creator.uid, content)
    }

    abstract fun reload()

    abstract fun save()

}