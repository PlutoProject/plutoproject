package ink.pmc.bedrockadaptive.delegations;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.title.GenericTitlePacket;
import io.netty.buffer.ByteBuf;
import net.bytebuddy.asm.Advice;

@SuppressWarnings("unused")
public final class TitlePacketsDecodeDelegation {

    @Advice.OnMethodEnter
    public static void decode(@Advice.This GenericTitlePacket packet, @Advice.Argument(0) ByteBuf buf, @Advice.Argument(1) ProtocolUtils.Direction direction, @Advice.Argument(2) ProtocolVersion version) {
        packet.setComponent(ComponentHolder.read(buf, version));
    }

}
