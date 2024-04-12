package ink.pmc.common.member

import ink.pmc.common.member.api.AuthType
import ink.pmc.common.member.api.BedrockAccount
import ink.pmc.common.member.api.WhitelistStatus
import ink.pmc.common.member.api.comment.CommentRepository
import ink.pmc.common.member.api.data.DataContainer
import ink.pmc.common.member.api.data.MemberModifier
import ink.pmc.common.member.api.punishment.PunishmentLogger
import ink.pmc.common.member.data.MemberModifierImpl
import ink.pmc.common.member.storage.MemberStorage
import java.time.Instant
import java.util.*

class MemberImpl(
    private val service: AbstractMemberService,
    override val uid: Long
) : AbstractMember() {

    override val storage: MemberStorage
        get() = service.lookupMember(uid)
    override val id: UUID
        get() = UUID.fromString(storage.id)
    override val name: String
        get() = storage.name
    override val whitelistStatus: WhitelistStatus
        get() = WhitelistStatus.valueOf(storage.whitelistStatus)
    override val authType: AuthType
        get() = AuthType.valueOf(storage.authType)
    override val createdAt: Instant
        get() = Instant.ofEpochMilli(storage.createdAt)
    override val lastJoinedAt: Instant?
        get() = if (storage.lastJoinedAt != null) Instant.ofEpochMilli(storage.lastJoinedAt!!) else null
    override val dataContainer: DataContainer
        get() = TODO("Not yet implemented")
    override val bedrockAccount: BedrockAccount?
        get() = TODO("Not yet implemented")
    override val bio: String?
        get() = storage.bio
    override val commentRepository: CommentRepository
        get() = TODO("Not yet implemented")
    override val punishmentLogger: PunishmentLogger
        get() = TODO("Not yet implemented")
    override val modifier: MemberModifier
        get() = MemberModifierImpl(this)

    override fun exemptWhitelist() {
        if (whitelistStatus == WhitelistStatus.NON_WHITELISTED || whitelistStatus == WhitelistStatus.WHITELISTED_BEFORE) {
            return
        }

        storage.whitelistStatus = WhitelistStatus.WHITELISTED_BEFORE.toString()
    }

    override fun grantWhitelist() {
        storage.whitelistStatus = WhitelistStatus.WHITELISTED.toString()
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