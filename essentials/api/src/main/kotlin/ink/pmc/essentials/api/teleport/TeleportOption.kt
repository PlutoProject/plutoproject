package ink.pmc.essentials.api.teleport

import kotlin.time.Duration

@Suppress("UNUSED")
data class TeleportOption(
    val expireAfter: Duration,
    val removeAfter: Duration
)