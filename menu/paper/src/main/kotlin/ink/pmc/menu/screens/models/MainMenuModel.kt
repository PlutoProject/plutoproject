package ink.pmc.menu.screens.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import ink.pmc.daily.api.Daily
import ink.pmc.essentials.api.home.Home
import ink.pmc.essentials.api.home.HomeManager
import ink.pmc.essentials.api.teleport.random.RandomTeleportManager
import ink.pmc.essentials.api.warp.Warp
import ink.pmc.essentials.api.warp.WarpManager
import ink.pmc.framework.utils.concurrent.submitAsync
import ink.pmc.menu.inspecting
import org.bukkit.entity.Player
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class MainMenuModel(private val player: Player) : ScreenModel {
    enum class Tab {
        HOME, ASSIST
    }

    sealed class PreferredHomeState {
        data object Loading : PreferredHomeState()
        class Ready(val home: Home) : PreferredHomeState()
        data object None : PreferredHomeState()
    }

    sealed class PreferredSpawnState {
        data object Loading : PreferredSpawnState()
        class Ready(val spawn: Warp) : PreferredSpawnState()
        data object None : PreferredSpawnState()
    }

    fun refreshPreferredHome() {
        submitAsync {
            val home = HomeManager.getPreferredHome(player)
            preferredHomeState = if (home != null) PreferredHomeState.Ready(home) else PreferredHomeState.None
        }
    }

    fun refreshPreferredSpawn() {
        submitAsync {
            val spawn = WarpManager.getPreferredSpawn(player)
            val defaultSpawn = WarpManager.getDefaultSpawn()
            preferredSpawnState = when {
                spawn != null -> PreferredSpawnState.Ready(spawn)
                defaultSpawn != null -> PreferredSpawnState.Ready(defaultSpawn)
                else -> PreferredSpawnState.None
            }
        }
    }

    fun refreshCheckInState() {
        submitAsync {
            isCheckedInToday = Daily.isCheckedInToday(player.uniqueId)
        }
    }

    private fun rtpCooldownRemaining(): Duration {
        return (RandomTeleportManager.getCooldown(player)?.remainingSeconds ?: 0).toDuration(DurationUnit.SECONDS)
    }

    fun refreshCooldownState() {
        rtpCooldownRemaining = rtpCooldownRemaining()
    }

    var tab by mutableStateOf(Tab.HOME)
    var preferredHomeState by mutableStateOf<PreferredHomeState>(PreferredHomeState.Loading)
    var preferredSpawnState by mutableStateOf<PreferredSpawnState>(PreferredSpawnState.Loading)
    var lookupModeEnabled by mutableStateOf(player.inspecting)
    var isCheckedInToday by mutableStateOf(false)
    var rtpCooldownRemaining by mutableStateOf(rtpCooldownRemaining())
}