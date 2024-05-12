package ink.pmc.common.member

import com.mongodb.client.model.Filters.eq
import ink.pmc.common.member.api.AuthType
import ink.pmc.common.member.api.BedrockAccount
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.WhitelistStatus
import ink.pmc.common.member.api.data.MemberModifier
import ink.pmc.common.member.data.AbstractBedrockAccount
import ink.pmc.common.member.data.AbstractDataContainer
import ink.pmc.common.member.data.BedrockAccountImpl
import ink.pmc.common.member.data.MemberModifierImpl
import ink.pmc.common.member.storage.BedrockAccountStorage
import ink.pmc.common.member.storage.MemberStorage
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId
import java.time.Instant
import java.util.*

class MemberImpl(
    private val service: AbstractMemberService,
    override var storage: MemberStorage
) : AbstractMember() {

    override var uid: Long = storage.uid
    override var id: UUID = UUID.fromString(storage.id)
    override var name: String = storage.name
    override var rawName: String = storage.rawName
    override var whitelistStatus: WhitelistStatus = WhitelistStatus.valueOf(storage.whitelistStatus)
    override val isWhitelisted: Boolean
        get() = whitelistStatus == WhitelistStatus.WHITELISTED
    override var authType: AuthType = AuthType.valueOf(storage.authType)
    override var createdAt: Instant = Instant.ofEpochMilli(storage.createdAt)
    override var lastJoinedAt: Instant? =
        if (storage.lastJoinedAt != null) Instant.ofEpochMilli(storage.lastJoinedAt!!) else null
    override var lastQuitedAt: Instant? =
        if (storage.lastQuitedAt != null) Instant.ofEpochMilli(storage.lastQuitedAt!!) else null
    override lateinit var dataContainer: AbstractDataContainer
    override var bedrockAccount: AbstractBedrockAccount? = null
    override var bio: String? = storage.bio
    override var isHidden: Boolean = storage.isHidden ?: false
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
        val storage = BedrockAccountStorage(
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

    override fun reload(storage: MemberStorage) {
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
        /*
        if (storage.bedrockAccount != null && bedrockAccount == null) {
            bedrockAccount = BedrockAccountImpl(this, memberService.lookupBedrockAccountStorage(storage.bedrockAccount!!)!!)
        }
        if (storage.bedrockAccount == null && bedrockAccount != null) {
            bedrockAccount = null
        }*/
        bio = storage.bio
        isHidden = storage.isHidden ?: false
        this.storage = storage
    }

}