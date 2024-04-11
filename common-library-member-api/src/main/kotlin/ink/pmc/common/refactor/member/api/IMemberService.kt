package ink.pmc.common.refactor.member.api

lateinit var instance: IMemberService

@Suppress("UNUSED")
object MemberService : IMemberService by instance

@Suppress("UNUSED")
interface IMemberService {



}