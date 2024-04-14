package ink.pmc.common.member.comment

import ink.pmc.common.member.AbstractMemberService
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.storage.CommentStorage
import kotlinx.coroutines.runBlocking
import java.time.Instant

class CommentImpl(private val service: AbstractMemberService, override val storage: CommentStorage) :
    AbstractComment() {

    override val id: Long = storage.id
    override val createdAt: Instant = Instant.ofEpochMilli(storage.createdAt)
    override val creator: Member = runBlocking { service.lookup(storage.creator)!! }
    override var content: String = storage.content
    override var isModified: Boolean = storage.isModified

}