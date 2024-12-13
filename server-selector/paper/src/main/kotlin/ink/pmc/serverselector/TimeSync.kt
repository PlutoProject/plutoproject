package ink.pmc.serverselector

import ink.pmc.framework.utils.concurrent.submitAsync
import ink.pmc.framework.utils.time.timezone
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.bukkit.entity.Player
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.seconds

private const val GAME_MIDNIGHT_OFFSET = 18000L
private val SERVER_FOUND_TIME = ZonedDateTime.of(
    2024, 2, 13, 3, 0, 0, 0, ZoneId.of("Asia/Shanghai")
)

private var timeSyncJob: Job? = null

fun stopTimeSync() {
    runCatching {
        timeSyncJob?.cancel()
    }
}

private fun Player.realWorldTimeToGameTime(): Long {
    val now = ZonedDateTime.now(timezone.toZoneId())
    val midnight = now.toLocalDate().atStartOfDay()
    val secondsSinceMidnight = Duration.between(midnight, now).seconds
    return secondsSinceMidnight * 20 + GAME_MIDNIGHT_OFFSET
}

private fun Player.getServerFoundGameTimeOffset(): Long {
    val now = ZonedDateTime.now(timezone.toZoneId())
    val foundTime = SERVER_FOUND_TIME.withZoneSameInstant(timezone.toZoneId())
    val daysSinceFound = Duration.between(foundTime, now).toDays()
    return daysSinceFound * 24000
}

private fun syncTime() {
    lobbyWorld.players.forEach {
        val time = it.realWorldTimeToGameTime() + it.getServerFoundGameTimeOffset()
        it.setPlayerTime(time, false)
    }
}

fun startTimeSync() {
    timeSyncJob = submitAsync {
        while (true) {
            delay(1.seconds)
            syncTime()
        }
    }
}