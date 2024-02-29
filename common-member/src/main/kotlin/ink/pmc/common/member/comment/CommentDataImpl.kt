package ink.pmc.common.member.comment

import ink.pmc.common.member.api.comment.CommentData

data class CommentDataImpl(
    override val comments: MutableCollection<CommentImpl> = mutableSetOf()
) : CommentData