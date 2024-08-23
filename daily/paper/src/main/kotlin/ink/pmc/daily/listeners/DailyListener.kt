package ink.pmc.daily.listeners

import ink.pmc.daily.api.Daily
import ink.pmc.daily.plugin
import ink.pmc.utils.concurrent.submitAsyncIO
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate

@Suppress("UNUSED")
object DailyListener : Listener, KoinComponent {

    private val daily by inject<Daily>()

    @EventHandler
    fun PlayerJoinEvent.e() {
        submitAsyncIO {
            val user = daily.getUser(player) ?: return@submitAsyncIO
            val now = LocalDate.now()
            val yesterday = now.minusDays(1)

            if (user.isCheckedInToday()) return@submitAsyncIO
            if (user.lastCheckInDate?.isAfter(now) == true) {
                user.resetCheckInTime()
                plugin.logger.warning("Abnormal check-in date detected for ${player.name}, reset to default")
                plugin.logger.warning("Is the system time incorrect?")
                return@submitAsyncIO
            }

            if (user.lastCheckInDate?.month != now.month || user.lastCheckInDate != yesterday) {
                user.clearAccumulation()
            }
        }
    }

    @EventHandler
    fun PlayerQuitEvent.e() {
        daily.unloadUser(player.uniqueId)
    }

}