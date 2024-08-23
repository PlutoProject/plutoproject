package ink.pmc.daily

import ink.pmc.advkt.component.text
import ink.pmc.advkt.playSound
import ink.pmc.advkt.send
import ink.pmc.daily.api.Daily
import ink.pmc.daily.api.DailyUser
import ink.pmc.daily.models.DailyHistoryModel
import ink.pmc.daily.models.DailyUserModel
import ink.pmc.daily.models.toModel
import ink.pmc.daily.repositories.DailyHistoryRepository
import ink.pmc.daily.repositories.DailyUserRepository
import ink.pmc.utils.currentUnixTimestamp
import ink.pmc.utils.player.uuid
import ink.pmc.utils.time.currentZoneId
import ink.pmc.utils.time.instant
import ink.pmc.utils.visual.mochaPink
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class DailyUserImpl(model: DailyUserModel) : DailyUser, KoinComponent {

    private val daily by inject<Daily>()
    private val historyRepo by inject<DailyHistoryRepository>()
    private val userRepo by inject<DailyUserRepository>()

    override val id: UUID = model.id.uuid
    override val player: OfflinePlayer by lazy { Bukkit.getOfflinePlayer(id) }
    override var lastCheckIn: LocalDateTime? =
        model.lastCheckIn?.let { LocalDateTime.ofInstant(it.instant, currentZoneId) }
    override val lastCheckInDate: LocalDate? get() = lastCheckIn?.toLocalDate()
    override var accumulatedDays: Int = model.accumulatedDays

    override suspend fun checkIn() {
        require(!isCheckedInToday()) { "User $id already checked in today!" }
        val history = DailyHistoryModel(
            owner = id.toString(),
            createdAt = currentUnixTimestamp
        )
        lastCheckIn = LocalDateTime.now()
        accumulatedDays++
        historyRepo.saveOrUpdate(history)
        update()
        daily.triggerPostCallback(this)
    }

    override suspend fun clearAccumulation() {
        accumulatedDays = 0
        update()
    }

    override suspend fun resetCheckInTime() {
        lastCheckIn = null
        update()
    }

    override fun isCheckedInToday(): Boolean {
        return lastCheckInDate == LocalDate.now()
    }

    override suspend fun update() {
        userRepo.saveOrUpdate(toModel())
    }

}