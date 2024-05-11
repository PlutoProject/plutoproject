package ink.pmc.common.member.storage

data class PunishmentStorage(
    val id: Long,
    val type: String,
    val time: Long,
    val belongs: Long,
    val executor: Long?,
    var isRevoked: Boolean,
)