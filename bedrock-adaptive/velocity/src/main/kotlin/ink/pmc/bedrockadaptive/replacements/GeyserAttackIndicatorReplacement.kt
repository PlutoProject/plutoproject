package ink.pmc.bedrockadaptive.replacements

import ink.pmc.bedrockadaptive.delegations.GeyserAttackIndicatorTitleDelegation
import ink.pmc.bedrockadaptive.utils.cooldownUtilsClass
import ink.pmc.utils.jvm.byteBuddy
import net.bytebuddy.asm.Advice
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy
import net.bytebuddy.matcher.ElementMatchers

object GeyserAttackIndicatorReplacement {

    fun init() {
        byteBuddy
            .redefine(cooldownUtilsClass)
            .visit(Advice.to(GeyserAttackIndicatorTitleDelegation::class.java).on(ElementMatchers.named("getTitle")))
            .make()
            .load(cooldownUtilsClass.classLoader, ClassReloadingStrategy.fromInstalledAgent())
    }
}