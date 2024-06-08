package ink.pmc.member.bedrock

import ink.pmc.common.member.delegations.SimpleFloodgateApiDelegations
import ink.pmc.utils.jvm.byteBuddy
import net.bytebuddy.asm.Advice
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy
import net.bytebuddy.matcher.ElementMatchers.named

object GeyserSimpleFloodgateApiReplacement {

    fun init() {
        byteBuddy
            .redefine(simpleFloodgateApiClass)
            .visit(Advice.to(SimpleFloodgateApiDelegations.AddPlayer::class.java).on(named("addPlayer")))
            .visit(Advice.to(SimpleFloodgateApiDelegations.IsFloodgateId::class.java).on(named("isFloodgateId")))
            .make()
            .load(simpleFloodgateApiClass.classLoader, ClassReloadingStrategy.fromInstalledAgent())
    }

}