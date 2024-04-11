package ink.pmc.common.refactor.member.api

import ink.pmc.common.refactor.member.api.data.History
import java.time.LocalDateTime

@Suppress("UNUSED")
data class BioHistory(override var lastModifiedAt: LocalDateTime?) : History<String>()