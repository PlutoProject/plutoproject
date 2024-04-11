package ink.pmc.common.refactor.member.api.punishment

import ink.pmc.common.refactor.member.api.data.History
import java.time.LocalDateTime

@Suppress("UNUSED")
data class PunishmentHistory(override var lastModifiedAt: LocalDateTime?) : History<PunishmentLog>()