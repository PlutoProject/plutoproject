package ink.pmc.common.member.punishment

import ink.pmc.common.member.AbstractMember
import ink.pmc.common.member.AbstractMemberService
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.punishment.Punishment
import ink.pmc.common.member.api.punishment.PunishmentLogger
import ink.pmc.common.member.api.punishment.PunishmentType
import ink.pmc.common.member.storage.PunishmentStorage
import org.bson.types.ObjectId

class PunishmentLoggerImpl(private val service: AbstractMemberService, private val member: AbstractMember) :
    PunishmentLogger {

    override val historyPunishments: Collection<Punishment>
        get() {
            val list = mutableListOf<Punishment>()

            member.storage.punishments.forEach {
                list.add(PunishmentImpl(service, service.cachedPunishment(it)!!))
            }

            return list
        }
    override val lastPunishment: Punishment?
        get() {
            if (member.storage.punishments.isEmpty()) {
                return null
            }

            return PunishmentImpl(service, service.cachedPunishment(member.storage.punishments.last())!!)
        }

    override fun create(type: PunishmentType, executor: Member): Punishment {
        val id = service.currentStatus.get().nextPunishment()

        val storage = PunishmentStorage(
            ObjectId(),
            id,
            type.toString(),
            System.currentTimeMillis(),
            member.uid,
            false,
            executor.uid
        )

        service.cachedStatus().increasePunishment()
        service.cachePunishment(id, storage)
        member.storage.punishments.add(id)

        return PunishmentImpl(service, storage)
    }

    override fun get(id: Long): Punishment? {
        if (!historyPunishments.any { it.id == id }) {
            return null
        }

        return PunishmentImpl(service, service.cachedPunishment(id)!!)
    }

    override fun exist(id: Long): Boolean {
        return service.cachedPunishment(id) != null
    }

    override fun revoke(punishment: Punishment) {
        if (punishment.isRevoked) {
            return
        }

        val impl = punishment as PunishmentImpl
        impl.storage.isRevoked = true
        service.cachePunishment(punishment.id, impl.storage)
    }

    override fun revoke(punishmentId: Long) {
        if (!exist(punishmentId)) {
            return
        }

        revoke(get(punishmentId)!!)
    }

}