package ink.pmc.essentials.api.economy

import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

@Suppress("UNUSED")
interface EconomyManager {

    suspend fun getAccount(player: OfflinePlayer): Account?

    suspend fun hasAccount(player: OfflinePlayer): Boolean

    suspend fun createAccount(player: Player): Account

    suspend fun removeAccount(offlinePlayer: OfflinePlayer)

}