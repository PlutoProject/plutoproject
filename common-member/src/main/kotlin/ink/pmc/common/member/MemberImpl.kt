package ink.pmc.common.member

import com.mongodb.client.model.Filters.eq
import ink.pmc.common.member.api.AuthType
import ink.pmc.common.member.api.BedrockAccount
import ink.pmc.common.member.api.WhitelistStatus
import ink.pmc.common.member.api.comment.CommentRepository
import ink.pmc.common.member.api.data.DataContainer
import ink.pmc.common.member.api.data.MemberModifier
import ink.pmc.common.member.api.punishment.PunishmentLogger
import ink.pmc.common.member.comment.CommentRepositoryImpl
import ink.pmc.common.member.data.BedrockAccountImpl
import ink.pmc.common.member.data.DataContainerImpl
import ink.pmc.common.member.data.MemberModifierImpl
import ink.pmc.common.member.punishment.PunishmentLoggerImpl
import ink.pmc.common.member.storage.BedrockAccountStorage
import ink.pmc.common.member.storage.MemberStorage
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId
import java.time.Instant
import java.util.*

class MemberImpl(
    private val service: AbstractMemberService,
    override val storage: MemberStorage
) : AbstractMember() {

    override val uid: Long
        get() = storage.uid
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
    override val dataContainer: DataContainer =
        DataContainerImpl(service, service.cachedDataContainer(storage.dataContainer)!!)
    override val bedrockAccount: BedrockAccount?
        get() {
            if (storage.bedrockAccount == null) {
                return null
            }

            return BedrockAccountImpl(service, service.cachedBedrockAccount(storage.bedrockAccount!!)!!)
        }
    override val bio: String?
        get() = storage.bio
    override val commentRepository: CommentRepository
        get() = CommentRepositoryImpl(service, this)
    override val punishmentLogger: PunishmentLogger
        get() = PunishmentLoggerImpl(service, this)
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
        if (bedrockAccount != null) {
            unlinkBedrock()
        }

        if (service.bedrockAccounts.find(eq("", xuid)).firstOrNull() != null) {
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

        service.cachedStatus().increaseBedrockAccount()
        service.cacheBedrockAccount(id, storage)
        this.storage.bedrockAccount = id

        return BedrockAccountImpl(service, storage)
    }

    override suspend fun unlinkBedrock() {
        if (bedrockAccount == null) {
            return
        }

        service.cachedBedrockAccount(storage.bedrockAccount!!)!!.removal = true
        storage.bedrockAccount = null
    }

    override suspend fun update() {
        service.update(this)
    }

    override suspend fun refresh() {
        service.refresh(this)
    }

}