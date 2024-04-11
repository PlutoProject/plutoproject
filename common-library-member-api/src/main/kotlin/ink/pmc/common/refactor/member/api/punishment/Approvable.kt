package ink.pmc.common.refactor.member.api.punishment

import ink.pmc.common.refactor.member.api.Member

@Suppress("UNUSED")
abstract class Approvable {

    private val approvers = mutableListOf<Long>()

    fun approvers(vararg uid: Long) {
        approvers.addAll(uid.toList())
    }

    fun approver(uid: Long) {
        approvers.add(uid)
    }

    fun approver(member: Member) {
        approver(member.uid)
    }

}