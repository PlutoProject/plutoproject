package ink.pmc.common.member.comment

import ink.pmc.common.member.api.Member
import ink.pmc.common.member.storage.CommentStorage
import java.time.Instant

class CommentImpl(override val storage: CommentStorage, override val creator: Member) :
    AbstractComment() {

    override val id: Long = storage.id
    override val createdAt: Instant = Instant.ofEpochMilli(storage.createdAt)
    override var content: String = storage.content
    override var isModified: Boolean = storage.isModified

}