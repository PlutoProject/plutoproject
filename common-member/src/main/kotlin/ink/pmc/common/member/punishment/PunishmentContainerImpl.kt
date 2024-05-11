package ink.pmc.common.member.punishment

import ink.pmc.common.member.AbstractMember
import ink.pmc.common.member.AbstractMemberService
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.punishment.Punishment
import ink.pmc.common.member.api.punishment.PunishmentType
import ink.pmc.common.member.memberService
import ink.pmc.common.member.storage.PunishmentStorage
import ink.pmc.common.utils.concurrent.submitAsyncIO

private const val PUNISHMENTS_LEY = "_punishments"

class PunishmentContainerImpl(private val service: AbstractMemberService, private val member: AbstractMember) :
    AbstractPunishmentContainer() {

    private val punishmentStorages: MutableCollection<PunishmentStorage> = mutableListOf()
    override lateinit var punishments: MutableCollection<Punishment>
    override var lastPunishment: Punishment? = null

    private fun loadPunishments() {
        punishmentStorages.clear()

        if (!member.dataContainer.contains(PUNISHMENTS_LEY)) {
            return
        }

        val storages =
            member.dataContainer.getCollection(PUNISHMENTS_LEY, PunishmentStorage::class.java)!!.toMutableList()
        punishmentStorages.addAll(storages)
    }

    init {
        submitAsyncIO {
            submitAsyncIO {
                loadPunishments()
                punishments = punishmentStorages.map {
                    PunishmentImpl(it, member, if (it.executor == null) null else memberService.lookup(it.executor)!!)
                }.toMutableList()
            }
        }
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

        service.currentStatus.increasePunishment()
        return PunishmentImpl(storage, member, executor)
    }

    override fun get(id: Long): Punishment? {
        if (!exist(id)) {
            return null
        }

        val punishment = punishments.first { it.id == id } as AbstractPunishment
        return punishment
    }

    override fun exist(id: Long): Boolean {
        return punishments.firstOrNull { it.id == id } != null
    }

    override fun revoke(punishment: Punishment) {
        if (punishment.isRevoked) {
            return
        }

        val impl = punishment as PunishmentImpl
        impl.storage.isRevoked = true
    }

    override fun reload() {
        loadPunishments()
    }

    override fun save() {
        member.dataContainer.remove(PUNISHMENTS_LEY)

        val storages = punishments.map {
            PunishmentStorage(
                it.id,
                it.type.toString(),
                it.time.toEpochMilli(),
                it.belongs.uid,
                it.executor?.uid,
                it.isRevoked
            )
        }

        member.dataContainer[PUNISHMENTS_LEY] = storages
    }

}