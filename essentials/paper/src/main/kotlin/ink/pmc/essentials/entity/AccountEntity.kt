package ink.pmc.essentials.entity

import ink.pmc.essentials.api.economy.Account
import kotlinx.serialization.Serializable

val Account.entity: AccountEntity
    get() = AccountEntity(
        owner = owner.uniqueId.toString(),
        balance = balance.toString(),
        createdAt = createdAt.toEpochMilli()
    )

@Serializable
data class AccountEntity(
    val owner: String,
    val balance: String,
    val createdAt: Long
)