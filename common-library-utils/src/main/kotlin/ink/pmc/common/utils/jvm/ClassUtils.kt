package ink.pmc.common.utils.jvm

import net.bytebuddy.ByteBuddy
import net.bytebuddy.agent.ByteBuddyAgent

@Suppress("UNUSED")
val byteBuddy = ByteBuddy().also {
    ByteBuddyAgent.install()
}