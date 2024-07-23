package ink.pmc.essentials.api.economy

import org.bukkit.OfflinePlayer
import java.math.BigDecimal
import java.time.Instant

@Suppress("UNUSED")
interface Account {

    val owner: OfflinePlayer
    val balance: BigDecimal
    val createdAt: Instant

    suspend fun withdraw(amount: BigDecimal): EconomyOperationResult

    suspend fun deposit(amount: BigDecimal): EconomyOperationResult

    suspend fun transfer(destination: Account): EconomyOperationResult

    suspend fun setBalance(amount: BigDecimal)

}