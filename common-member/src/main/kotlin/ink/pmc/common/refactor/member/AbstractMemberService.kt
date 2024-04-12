package ink.pmc.common.refactor.member

import com.mongodb.kotlin.client.coroutine.MongoCollection
import ink.pmc.common.refactor.member.api.IMemberService
import ink.pmc.common.refactor.member.storage.*

abstract class AbstractMemberService : IMemberService {

    abstract val status: MongoCollection<StatusStorage>
    abstract val members: MongoCollection<MemberStorage>
    abstract val punishments: MongoCollection<PunishmentStorage>
    abstract val comments: MongoCollection<CommentStorage>
    abstract val dataContainers: MongoCollection<DataContainerStorage>
    abstract val bedrockAccounts: MongoCollection<BedrockAccountStorage>

    abstract suspend fun currentStatus(): StatusStorage

    abstract suspend fun updateStatus(new: StatusStorage)

}