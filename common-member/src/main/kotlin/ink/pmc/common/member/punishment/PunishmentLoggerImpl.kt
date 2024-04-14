package ink.pmc.common.member.punishment

import ink.pmc.common.member.AbstractMember
import ink.pmc.common.member.AbstractMemberService
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.punishment.Punishment
import ink.pmc.common.member.api.punishment.PunishmentLogger
import ink.pmc.common.member.api.punishment.PunishmentType
import ink.pmc.common.member.storage.PunishmentStorage
import kotlinx.coroutines.runBlocking
import org.bson.types.ObjectId

class PunishmentLoggerImpl(private val service: AbstractMemberService, private val member: AbstractMember) :
    PunishmentLogger {

    override val historyPunishments: Collection<Punishment> = runBlocking {
        val list = mutableListOf<Punishment>()
        member.storage.punishments.forEach {
            list.add(PunishmentImpl(service, service.lookupPunishmentStorage(it)!!))
        }
        list
    }
    override var lastPunishment: Punishment? = runBlocking {
        if (member.storage.punishments.isEmpty()) {
            return@runBlocking null
        }
        PunishmentImpl(service, service.lookupPunishmentStorage(member.storage.punishments.last())!!)
    }

    override fun create(type: PunishmentType, executor: Member): Punishment {
        val id = service.currentStatus.nextPunishment()

        val storage = PunishmentStorage(
            ObjectId(),
            id,
            type.toString(),
            System.currentTimeMillis(),
            member.uid,
            false,
            executor.uid
        )

        service.currentStatus.increasePunishment()
        member.storage.punishments.add(id)

        return PunishmentImpl(service, storage)
    }

    override fun get(id: Long): Punishment? {
        if (!historyPunishments.any { it.id == id }) {
            return null
        }

        val punishment = historyPunishments.first { it.id == id }
        return punishment
    }

    override fun exist(id: Long): Boolean {
        return historyPunishments.firstOrNull() { it.id == id } != null
    }

    override fun revoke(punishment: Punishment) {
        if (punishment.isRevoked) {
            return
        }

        val impl = punishment as PunishmentImpl
        impl.storage.isRevoked = true
    }

    override fun revoke(punishmentId: Long) {
        if (!exist(punishmentId)) {
            return
        }

        revoke(get(punishmentId)!!)
    }

}