package ink.pmc.utils.multiplaform.player.velocity

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import ink.pmc.utils.multiplaform.SenderWrapper
import ink.pmc.utils.multiplaform.player.PlayerWrapper

val CommandSource.wrapped: VelocitySenderWrapper<CommandSource>
    get() = VelocitySenderWrapper(this)

val Player.wrapped: VelocityPlayerWrapper
    get() = VelocityPlayerWrapper(this)

val SenderWrapper<*>.velocity: CommandSource
    get() = original as CommandSource

val PlayerWrapper<*>.velocity: Player
    get() = original as Player