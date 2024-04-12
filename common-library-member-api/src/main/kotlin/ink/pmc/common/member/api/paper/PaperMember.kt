package ink.pmc.common.member.api.paper

import ink.pmc.common.member.api.Member
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

@Suppress("UNUSED")
val Member.player: OfflinePlayer
    get() = Bukkit.getOfflinePlayer(this.id)