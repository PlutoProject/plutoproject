package ink.pmc.bedrockadaptive.replacements

import com.velocitypowered.proxy.protocol.packet.title.GenericTitlePacket
import ink.pmc.bedrockadaptive.delegations.TitlePacketsDecodeDelegation
import ink.pmc.utils.jvm.byteBuddy
import net.bytebuddy.asm.Advice
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy
import net.bytebuddy.implementation.StubMethod
import net.bytebuddy.matcher.ElementMatchers.named

object TitlePacketDecodeReplacement {

    fun init() {
        /*
        * 由 GenericTitlePacket 派生的几个 Title 包都没有实现解码。
        * 原因未知，但这会导致我们无法操作这些包。
        * 在这里强行为该类插入 decode 的实现。
        * */
        byteBuddy
            .redefine(GenericTitlePacket::class.java)
            .method(named("decode"))
            .intercept(Advice.to(TitlePacketsDecodeDelegation::class.java).wrap(StubMethod.INSTANCE))
            .make()
            .load(GenericTitlePacket::class.java.classLoader, ClassReloadingStrategy.fromInstalledAgent())/**/
    }
}