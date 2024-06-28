package ink.pmc.member

import com.mongodb.client.model.Filters.eq
import ink.pmc.member.api.AuthType
import ink.pmc.member.api.BedrockAccount
import ink.pmc.member.api.Member
import ink.pmc.member.api.WhitelistStatus
import ink.pmc.member.api.data.MemberModifier
import ink.pmc.member.data.AbstractBedrockAccount
import ink.pmc.member.data.AbstractDataContainer
import ink.pmc.member.data.BedrockAccountImpl
import ink.pmc.member.data.MemberModifierImpl
import ink.pmc.member.storage.BedrockAccountBean
import ink.pmc.member.storage.MemberBean
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId
import java.time.Instant
import java.util.*

class MemberImpl(
    private val service: AbstractMemberService,
    override var bean: MemberBean
) : AbstractMember() {

    override var uid: Long = bean.uid
    override var id: UUID = UUID.fromString(bean.id)
    override var name: String = bean.name
    override var rawName: String = bean.rawName
    override var whitelistStatus: WhitelistStatus = WhitelistStatus.valueOf(bean.whitelistStatus)
    override val isWhitelisted: Boolean
        get() = whitelistStatus == WhitelistStatus.WHITELISTED
    override var authType: AuthType = AuthType.valueOf(bean.authType)
    override var createdAt: Instant = Instant.ofEpochMilli(bean.createdAt)
    override var lastJoinedAt: Instant? =
        if (bean.lastJoinedAt != null) Instant.ofEpochMilli(bean.lastJoinedAt!!) else null
    override var lastQuitedAt: Instant? =
        if (bean.lastQuitedAt != null) Instant.ofEpochMilli(bean.lastQuitedAt!!) else null
    override lateinit var dataContainer: AbstractDataContainer
    override var bedrockAccount: AbstractBedrockAccount? = null
    override var bio: String? = bean.bio
    override var isHidden: Boolean = bean.isHidden ?: false
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

    override suspend fun linkBedrock(xuid: String, gamertag: String): BedrockAccount? {
        if (bedrockAccount != null) {
            unlinkBedrock()
        }

        if (service.bedrockAccounts.find(eq("", xuid)).firstOrNull() != null) {
            return null
        }

        val id = service.currentStatus.nextBedrockAccount()
        val storage = BedrockAccountBean(
            ObjectId(),
            id,
            uid,
            xuid,
            gamertag,
            true
        )

        service.currentStatus.increaseBedrockAccount()
        val account = BedrockAccountImpl(this, storage)
        bedrockAccount = account

        return account
    }

    override fun unlinkBedrock() {
        if (bedrockAccount == null) {
            return
        }

        bedrockAccount = null
    }

    override suspend fun save() {
        service.save(this)
    }

    override suspend fun sync(): Member? {
        return service.sync(this)
    }

    override fun reload(storage: MemberBean) {
        uid = storage.uid
        id = UUID.fromString(storage.id)
        name = storage.name
        rawName = storage.rawName
        whitelistStatus = WhitelistStatus.valueOf(storage.whitelistStatus)
        authType = AuthType.valueOf(storage.authType)
        createdAt = Instant.ofEpochMilli(storage.createdAt)
        lastJoinedAt = if (storage.lastJoinedAt != null) {
            Instant.ofEpochMilli(storage.lastJoinedAt!!)
        } else {
            null
        }
        lastQuitedAt = if (storage.lastQuitedAt != null) {
            Instant.ofEpochMilli(storage.lastQuitedAt!!)
        } else {
            null
        }
        bio = storage.bio
        isHidden = storage.isHidden ?: false
        this.bean = storage
    }

    override fun createBean(): MemberBean {
        return bean.copy(
            uid = this.uid,
            id = this.id.toString(),
            name = this.name,
            rawName = this.rawName,
            whitelistStatus = this.whitelistStatus.toString(),
            authType = this.authType.toString(),
            createdAt = this.createdAt.toEpochMilli(),
            lastJoinedAt = this.lastJoinedAt?.toEpochMilli(),
            lastQuitedAt = this.lastQuitedAt?.toEpochMilli(),
            dataContainer = this.dataContainer.id,
            bedrockAccount = this.bedrockAccount?.id,
            bio = this.bio,
            isHidden = this.isHidden,
            new = false
        )
    }
}