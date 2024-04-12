package ink.pmc.common.member.comment

import ink.pmc.common.member.AbstractMemberService
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.comment.Comment
import ink.pmc.common.member.storage.CommentStorage
import kotlinx.coroutines.runBlocking
import java.time.Instant

class CommentImpl(private val service: AbstractMemberService, override val storage: CommentStorage) : AbstractComment() {

    override val id: Long
        get() = storage.id
    override val createdAt: Instant
        get() = Instant.ofEpochMilli(storage.createdAt)
    override val creator: Member
        get() = runBlocking { service.lookup(storage.creator)!! }
    override val content: String
        get() = storage.content
    override val isModified: Boolean
        get() = storage.isModified

}