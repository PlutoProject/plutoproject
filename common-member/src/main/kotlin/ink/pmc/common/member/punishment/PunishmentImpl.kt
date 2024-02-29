package ink.pmc.common.member.punishment

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ink.pmc.common.member.api.punishment.PardonReason
import ink.pmc.common.member.api.punishment.Punishment
import ink.pmc.common.member.api.punishment.PunishmentReason
import ink.pmc.common.member.api.punishment.PunishmentType
import java.util.*

data class PunishmentImpl @JsonCreator constructor(
    @JsonProperty("id") override val id: Long,
    @JsonProperty("owner") override val owner: UUID,
    @JsonProperty("type") override val type: PunishmentType,
    @JsonProperty("reason") override var reason: PunishmentReason = PunishmentReason.NONE,
    @JsonProperty("executeDate") override val executeDate: Date = Date(),
    @JsonProperty("isPardoned") override var isPardoned: Boolean = false,
    @JsonProperty("pardonReason") override var pardonReason: PardonReason? = null,
) : Punishment