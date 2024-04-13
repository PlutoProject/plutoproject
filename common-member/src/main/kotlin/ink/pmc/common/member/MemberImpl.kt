package ink.pmc.common.member

import com.mongodb.client.model.Filters.eq
import ink.pmc.common.member.api.AuthType
import ink.pmc.common.member.api.BedrockAccount
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.WhitelistStatus
import ink.pmc.common.member.api.comment.CommentRepository
import ink.pmc.common.member.api.data.DataContainer
import ink.pmc.common.member.api.data.MemberModifier
import ink.pmc.common.member.api.punishment.PunishmentLogger
import ink.pmc.common.member.comment.CommentRepositoryImpl
import ink.pmc.common.member.data.AbstractBedrockAccount
import ink.pmc.common.member.data.BedrockAccountImpl
import ink.pmc.common.member.data.MemberModifierImpl
import ink.pmc.common.member.punishment.PunishmentLoggerImpl
import ink.pmc.common.member.storage.BedrockAccountStorage
import ink.pmc.common.member.storage.MemberStorage
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.bson.types.ObjectId
import java.time.Instant
import java.util.*

class MemberImpl(
    private val service: AbstractMemberService,
    override val storage: MemberStorage,
    override val dataContainer: DataContainer,
    override var bedrockAccount: BedrockAccount?
) : AbstractMember() {

    override val uid: Long = storage.uid
    override val id: UUID = UUID.fromString(storage.id)
    override var name: String = storage.name
    override var whitelistStatus: WhitelistStatus = WhitelistStatus.valueOf(storage.whitelistStatus)
    override val authType: AuthType = AuthType.valueOf(storage.authType)
    override var createdAt: Instant = Instant.ofEpochMilli(storage.createdAt)
    override var lastJoinedAt: Instant? =
        if (storage.lastJoinedAt != null) Instant.ofEpochMilli(storage.lastJoinedAt!!) else null
    override var bio: String? = storage.bio
    override val commentRepository: CommentRepository = CommentRepositoryImpl(service, this)
    override val punishmentLogger: PunishmentLogger = PunishmentLoggerImpl(service, this)
    override val modifier: MemberModifier = MemberModifierImpl(this)

    override fun exemptWhitelist() {
        if (whitelistStatus == WhitelistStatus.NON_WHITELISTED || whitelistStatus == WhitelistStatus.WHITELISTED_BEFORE) {
            return
        }

        storage.whitelistStatus = WhitelistStatus.WHITELISTED_BEFORE.toString()
    }

    override fun grantWhitelist() {
        storage.whitelistStatus = WhitelistStatus.WHITELISTED.toString()
    }

    override suspend fun isValid(): Boolean {
        return service.members.find(eq("uid", uid)).firstOrNull() != null
    }

    override fun linkBedrock(xuid: String, gamertag: String): BedrockAccount? {
        if (bedrockAccount != null) {
            unlinkBedrock()
        }

        if (runBlocking { service.bedrockAccounts.find(eq("", xuid)).firstOrNull() } != null) {
            return null
        }

        val id = service.currentStatus.get().nextBedrockAccount()
        val storage = BedrockAccountStorage(
            ObjectId(),
            id,
            uid,
            xuid,
            gamertag
        )

        service.currentStatus.get().increaseBedrockAccount()
        val account = BedrockAccountImpl(service, storage)
        bedrockAccount = account

        return account
    }

    override fun unlinkBedrock() {
        if (bedrockAccount == null) {
            return
        }

        val bedrockAccountStorage = (bedrockAccount as AbstractBedrockAccount).storage
        dirtyBedrockAccounts.add(bedrockAccountStorage)
        bedrockAccount = null
    }

    override suspend fun update() {
        service.update(this)
    }

    override suspend fun refresh(): Member? {
        return service.refresh(this)
    }

}