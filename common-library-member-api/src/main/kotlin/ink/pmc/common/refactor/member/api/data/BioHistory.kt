package ink.pmc.common.refactor.member.api.data

import java.time.LocalDateTime

@Suppress("UNUSED")
data class BioHistory(override var lastModifiedAt: LocalDateTime?) : History<String>()