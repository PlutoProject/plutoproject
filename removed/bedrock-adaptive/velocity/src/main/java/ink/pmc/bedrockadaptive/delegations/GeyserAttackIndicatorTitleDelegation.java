package ink.pmc.bedrockadaptive.delegations;

import net.bytebuddy.asm.Advice;

@SuppressWarnings("unused")
public final class GeyserAttackIndicatorTitleDelegation {

    @Advice.OnMethodExit
    public static void getTitle(@Advice.Return(readOnly = false) String text, @Advice.Argument(0) Object session) {
        text = text.replace("§8", "§i");
    }

}
