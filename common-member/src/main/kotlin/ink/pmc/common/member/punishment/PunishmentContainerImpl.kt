package ink.pmc.common.member.punishment

import ink.pmc.common.member.AbstractMember
import ink.pmc.common.member.AbstractMemberService
import ink.pmc.common.member.PUNISHMENTS_LEY
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.punishment.Punishment
import ink.pmc.common.member.api.punishment.PunishmentType
import ink.pmc.common.member.storage.PunishmentStorage

class PunishmentContainerImpl(private val service: AbstractMemberService, private val member: AbstractMember) :
    AbstractPunishmentContainer() {

    override val punishments: MutableCollection<AbstractPunishment>
        get() = lookup()
    override var lastPunishment: Punishment? = if (punishments.isEmpty()) null else punishments.last()

    private fun update(list: MutableCollection<AbstractPunishment>) {
        member.dataContainer[PUNISHMENTS_LEY] = list.map { it.storage }
    }

    private fun lookup(): MutableCollection<AbstractPunishment> {
        return member.dataContainer.getCollection(PUNISHMENTS_LEY, PunishmentStorage::class.java)!!.map {
            PunishmentImpl(it, member)
        }.toMutableList()
    }

    override fun create(type: PunishmentType, executor: Member?): Punishment {
        val id = service.currentStatus.nextPunishment()

        val storage = PunishmentStorage(
            id,
            type.toString(),
            System.currentTimeMillis(),
            member.uid,
            executor?.uid,
            false
        )

        val punishment = PunishmentImpl(storage, member)
        val updated = lookup().apply { add(punishment) }
        update(updated)

        service.currentStatus.increasePunishment()
        return punishment
    }

    override fun get(id: Long): Punishment? {
        if (!exist(id)) {
            return null
        }

        val punishment = punishments.first { it.id == id }
        return punishment
    }

    override fun exist(id: Long): Boolean {
        return punishments.firstOrNull { it.id == id } != null
    }

    override fun revoke(punishment: Punishment) {
        if (!exist(punishment.id)) {
            return
        }

        if (punishment.isRevoked) {
            return
        }

        val updatedPunishment = (punishment as PunishmentImpl).apply { isRevoked = true }
        val updated = punishments.toMutableList().apply {
            val index = indexOfFirst { it.id == punishment.id }
            this.add(index, updatedPunishment)
        }
        update(updated)
    }

}