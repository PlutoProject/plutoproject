package ink.pmc.essentials.api.economy

import org.bukkit.OfflinePlayer

@Suppress("UNUSED")
interface EconomyManager {

    suspend fun getBalanceTop(entries: Int): List<Account>

    suspend fun getAccount(player: OfflinePlayer): Account?

    suspend fun hasAccount(player: OfflinePlayer): Boolean

    suspend fun getAccountCount(): Int

    suspend fun createAccount(player: OfflinePlayer): Account

    suspend fun removeAccount(offlinePlayer: OfflinePlayer)

}