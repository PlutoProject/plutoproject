package ink.pmc.bedrockadaptive.replacements

import com.velocitypowered.proxy.protocol.packet.chat.SystemChatPacket
import ink.pmc.bedrockadaptive.delegations.SystemChatPacketDecodeDelegation
import ink.pmc.utils.jvm.byteBuddy
import net.bytebuddy.asm.Advice
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy
import net.bytebuddy.implementation.StubMethod
import net.bytebuddy.matcher.ElementMatchers.named

/*
* Velocity 对于该数据包的解码有问题。
* 由于 Protocolize 修改数据包会让包再发出去之前解码因此，因此得到了错误的结果。
* */
object SystemChatPacketDecodeReplacement {

    fun init() {
        byteBuddy
            .redefine(SystemChatPacket::class.java)
            .method(named("decode"))
            .intercept(Advice.to(SystemChatPacketDecodeDelegation::class.java).wrap(StubMethod.INSTANCE))
            .make()
            .load(SystemChatPacket::class.java.classLoader, ClassReloadingStrategy.fromInstalledAgent())
    }
}