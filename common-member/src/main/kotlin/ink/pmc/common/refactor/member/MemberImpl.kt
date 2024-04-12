package ink.pmc.common.refactor.member

import ink.pmc.common.refactor.member.api.AuthType
import ink.pmc.common.refactor.member.api.BedrockAccount
import ink.pmc.common.refactor.member.api.WhitelistStatus
import ink.pmc.common.refactor.member.api.comment.CommentRepository
import ink.pmc.common.refactor.member.api.data.DataContainer
import ink.pmc.common.refactor.member.api.data.MemberModifier
import ink.pmc.common.refactor.member.api.punishment.PunishmentLogger
import ink.pmc.common.refactor.member.storage.MemberStorage
import java.time.LocalDateTime
import java.util.*

class MemberImpl(override val storage: MemberStorage) : AbstractMember() {

    override val uid: Long
        get() = TODO("Not yet implemented")
    override val id: UUID
        get() = TODO("Not yet implemented")
    override val name: String
        get() = TODO("Not yet implemented")
    override val whitelistStatus: WhitelistStatus
        get() = TODO("Not yet implemented")
    override val authType: AuthType
        get() = TODO("Not yet implemented")
    override val createdAt: LocalDateTime
        get() = TODO("Not yet implemented")
    override val lastJoinedAt: LocalDateTime?
        get() = TODO("Not yet implemented")
    override val dataContainer: DataContainer
        get() = TODO("Not yet implemented")
    override val bedrockAccount: BedrockAccount?
        get() = TODO("Not yet implemented")
    override val bio: String?
        get() = TODO("Not yet implemented")
    override val commentRepository: CommentRepository
        get() = TODO("Not yet implemented")
    override val punishmentLogger: PunishmentLogger
        get() = TODO("Not yet implemented")
    override val modifier: MemberModifier
        get() = TODO("Not yet implemented")

    override fun exemptWhitelist() {
        TODO("Not yet implemented")
    }

    override fun grantWhitelist() {
        TODO("Not yet implemented")
    }

    override suspend fun linkBedrock(xuid: String, gamertag: String): BedrockAccount? {
        TODO("Not yet implemented")
    }

    override suspend fun unlinkBedrock() {
        TODO("Not yet implemented")
    }

    override suspend fun update() {
        TODO("Not yet implemented")
    }

    override suspend fun refresh() {
        TODO("Not yet implemented")
    }

}