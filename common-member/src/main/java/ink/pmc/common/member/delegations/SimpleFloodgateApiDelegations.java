package ink.pmc.common.member.delegations;

import ink.pmc.member.bedrock.GeyserUtilsKt;
import net.bytebuddy.asm.Advice;

import java.util.UUID;

@SuppressWarnings("unused")
public final class SimpleFloodgateApiDelegations {

    public static class AddPlayer {
        @Advice.OnMethodEnter
        public static void addPlayer(@Advice.Argument(0) Object floodgatePlayer) {
            GeyserUtilsKt.addFloodgatePlayer(floodgatePlayer);
        }
    }

    public static class IsFloodgateId {
        @Advice.OnMethodEnter
        public static boolean isFloodgateId(@Advice.Argument(0) UUID uuid) {
            return true;
        }
    }

}
