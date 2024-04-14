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
import ink.pmc.common.member.data.DataContainerImpl
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
    override val storage: MemberStorage
) : AbstractMember() {

    override val uid: Long = storage.uid
    override val id: UUID = UUID.fromString(storage.id)
    override var name: String = storage.name
    override val rawName: String = storage.rawName
    override var whitelistStatus: WhitelistStatus = WhitelistStatus.valueOf(storage.whitelistStatus)
    override val isWhitelisted: Boolean
        get() = whitelistStatus == WhitelistStatus.WHITELISTED
    override val authType: AuthType = AuthType.valueOf(storage.authType)
    override var createdAt: Instant = Instant.ofEpochMilli(storage.createdAt)
    override var lastJoinedAt: Instant? =
        if (storage.lastJoinedAt != null) Instant.ofEpochMilli(storage.lastJoinedAt!!) else null
    override var lastQuitedAt: Instant? =
        if (storage.lastQuitedAt != null) Instant.ofEpochMilli(storage.lastQuitedAt!!) else null
    override val dataContainer: DataContainer =
        DataContainerImpl(service, runBlocking { service.lookupDataContainerStorage(storage.dataContainer)!! })
    override var bedrockAccount: BedrockAccount? = if (storage.bedrockAccount == null) {
        null
    } else {
        BedrockAccountImpl(service, runBlocking { service.lookupBedrockAccount(storage.bedrockAccount!!)!! })
    }
    override var bio: String? = storage.bio
    override val commentRepository: CommentRepository = CommentRepositoryImpl(service, this)
    override val punishmentLogger: PunishmentLogger = PunishmentLoggerImpl(service, this)
    override val modifier: MemberModifier = MemberModifierImpl(this)

    override fun exemptWhitelist() {
        if (whitelistStatus == WhitelistStatus.NON_WHITELISTED || whitelistStatus == WhitelistStatus.WHITELISTED_BEFORE) {
            return
        }

        whitelistStatus = WhitelistStatus.WHITELISTED_BEFORE
    }

    override fun grantWhitelist() {
        whitelistStatus = WhitelistStatus.WHITELISTED
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

        val id = service.currentStatus.nextBedrockAccount()
        val storage = BedrockAccountStorage(
            ObjectId(),
            id,
            uid,
            xuid,
            gamertag
        )

        service.currentStatus.increaseBedrockAccount()
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