package ink.pmc.utils.multiplaform.player.velocity

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player

val CommandSource.wrapped: VelocitySenderWrapper<CommandSource>
    get() = VelocitySenderWrapper(this)

val Player.wrapped: VelocityPlayerWrapper
    get() = VelocityPlayerWrapper(this)