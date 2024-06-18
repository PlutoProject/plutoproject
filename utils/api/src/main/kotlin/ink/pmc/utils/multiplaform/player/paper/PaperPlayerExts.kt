package ink.pmc.utils.multiplaform.player.paper

import ink.pmc.utils.multiplaform.SenderWrapper
import ink.pmc.utils.multiplaform.player.PlayerWrapper
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

val CommandSender.wrapped: PaperSenderWrapper<CommandSender>
    get() = PaperSenderWrapper(this)

val Player.wrapped: PaperPlayerWrapper
    get() = PaperPlayerWrapper(this)

val SenderWrapper<*>.paper: CommandSender
    get() = original as CommandSender

val PlayerWrapper<*>.paper: Player
    get() = original as Player