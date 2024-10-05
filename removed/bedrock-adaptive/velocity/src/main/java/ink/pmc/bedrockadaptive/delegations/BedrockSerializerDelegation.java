package ink.pmc.bedrockadaptive.delegations;

import ink.pmc.bedrockadaptive.replacements.BedrockColorSerializerReplacementKt;
import net.bytebuddy.asm.Advice;
import net.kyori.adventure.text.Component;

@SuppressWarnings("unused")
public final class BedrockSerializerDelegation {

    public static class CovertMessage {
        @Advice.OnMethodEnter
        public static void convertMessage(@Advice.Local("legacy") String legacy, @Advice.Argument(0) Component message) {
            legacy = BedrockColorSerializerReplacementKt.getBedrockSerializer().serialize(message);
        }
    }

    public static class ConvertToJavaMessage {
        @Advice.OnMethodEnter
        public static void convertToJavaMessage(@Advice.Local("component") Component component, @Advice.Argument(0) String message) {
            component = BedrockColorSerializerReplacementKt.getBedrockSerializer().deserialize(message);
        }
    }

}
