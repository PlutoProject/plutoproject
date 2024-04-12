package ink.pmc.common.member.punishment

import ink.pmc.common.member.AbstractMemberService
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.punishment.Punishment
import ink.pmc.common.member.api.punishment.PunishmentType
import ink.pmc.common.member.storage.PunishmentStorage
import kotlinx.coroutines.runBlocking
import java.time.Instant

class PunishmentImpl(private val service: AbstractMemberService, val storage: PunishmentStorage) : Punishment {

    override val id: Long
        get() = storage.id
    override val type: PunishmentType
        get() = PunishmentType.valueOf(storage.type)
    override val time: Instant
        get() = Instant.ofEpochMilli(storage.time)
    override val belongs: Member
        get() = runBlocking { service.lookup(storage.belongs)!! }
    override val isRevoked: Boolean
        get() = storage.isRevoked
    override val executor: Member
        get() = runBlocking { service.lookup(storage.executor)!! }

}