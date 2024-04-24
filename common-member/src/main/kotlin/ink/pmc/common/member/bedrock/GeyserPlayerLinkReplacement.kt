package ink.pmc.common.member.bedrock

import ink.pmc.common.member.delegations.MemberPlayerLinkDelegations
import ink.pmc.common.utils.jvm.byteBuddy
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.matcher.ElementMatchers.named

object GeyserPlayerLinkReplacement {

    private val memberPlayerLink = byteBuddy
        .subclass(Any::class.java)
        .implement(playerLinkClass)
        .method(named("load"))
        .intercept(MethodDelegation.to(MemberPlayerLinkDelegations::class.java))
        .method(named("getLinkedPlayer"))
        .intercept(MethodDelegation.to(MemberPlayerLinkDelegations::class.java))
        .method(named("isLinkedPlayer"))
        .intercept(MethodDelegation.to(MemberPlayerLinkDelegations::class.java))
        .method(named("linkPlayer"))
        .intercept(MethodDelegation.to(MemberPlayerLinkDelegations::class.java))
        .method(named("createLinkRequest"))
        .intercept(MethodDelegation.to(MemberPlayerLinkDelegations::class.java))
        .method(named("verifyLinkRequest"))
        .intercept(MethodDelegation.to(MemberPlayerLinkDelegations::class.java))
        .method(named("getName"))
        .intercept(MethodDelegation.to(MemberPlayerLinkDelegations::class.java))
        .method(named("isEnabled"))
        .intercept(MethodDelegation.to(MemberPlayerLinkDelegations::class.java))
        .method(named("getVerifyLinkTimeout"))
        .intercept(MethodDelegation.to(MemberPlayerLinkDelegations::class.java))
        .method(named("isAllowLinking"))
        .intercept(MethodDelegation.to(MemberPlayerLinkDelegations::class.java))
        .method(named("stop"))
        .intercept(MethodDelegation.to(MemberPlayerLinkDelegations::class.java))
        .make()
        .load(playerLinkClass.classLoader, ClassReloadingStrategy.fromInstalledAgent())
        .loaded
        .getDeclaredConstructor()
        .newInstance()

    fun init() {
        replacePlayerLink(memberPlayerLink)
    }

}