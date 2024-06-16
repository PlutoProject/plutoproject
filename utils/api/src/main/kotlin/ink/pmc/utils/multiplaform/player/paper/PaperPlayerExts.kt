package ink.pmc.utils.multiplaform.player.paper

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

val CommandSender.wrapped: PaperSenderWrapper<CommandSender>
    get() = PaperSenderWrapper(this)

val Player.wrapped: PaperPlayerWrapper
    get() = PaperPlayerWrapper(this)