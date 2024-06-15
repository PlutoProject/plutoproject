package ink.pmc.member

import java.util.logging.Logger

var disabled = true
lateinit var serverLogger: Logger
lateinit var memberService: BaseMemberServiceImpl

fun safeDisable() {
    memberService.loadedMembers.synchronous().invalidateAll()
    memberService.loadedMembers.synchronous().cleanUp()
}