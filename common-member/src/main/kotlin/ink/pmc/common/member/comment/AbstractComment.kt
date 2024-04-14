package ink.pmc.common.member.comment

import ink.pmc.common.member.api.comment.Comment
import ink.pmc.common.member.storage.CommentStorage

abstract class AbstractComment : Comment {

    abstract val storage: CommentStorage

    override fun equals(other: Any?): Boolean {
        if (other !is Comment) {
            return false
        }

        return other.id == id
    }

    override fun hashCode(): Int {
        return storage.hashCode()
    }

}