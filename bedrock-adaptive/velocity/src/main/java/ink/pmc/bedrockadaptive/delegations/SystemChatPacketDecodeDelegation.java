package ink.pmc.bedrockadaptive.delegations;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.packet.chat.ChatType;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.chat.SystemChatPacket;
import io.netty.buffer.ByteBuf;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
public final class SystemChatPacketDecodeDelegation {

    @Advice.OnMethodEnter
    public static void decode(@Advice.This SystemChatPacket packet, @Advice.Argument(0) ByteBuf buf, @Advice.Argument(1) ProtocolUtils.Direction direction, @Advice.Argument(2) ProtocolVersion version) {
        try {
            Class<?> packetClass = SystemChatPacket.class;
            Field componentField = packetClass.getDeclaredField("component");
            Field chatTypeField = packetClass.getDeclaredField("type");
            componentField.setAccessible(true);
            chatTypeField.setAccessible(true);
            componentField.set(packet, ComponentHolder.read(buf, version));

            if (version.noLessThan(ProtocolVersion.MINECRAFT_1_19_1)) {
                boolean isGameInfo = buf.readBoolean();

                if (isGameInfo) {
                    chatTypeField.set(packet, ChatType.GAME_INFO);
                    return;
                }

                chatTypeField.set(packet, ChatType.SYSTEM);
                return;
            }

            chatTypeField.set(packet, ChatType.values()[ProtocolUtils.readVarInt(buf)]);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode!");
        }
    }

}