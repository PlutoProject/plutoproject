package ink.pmc.menu.screens.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import ink.pmc.essentials.api.Essentials
import ink.pmc.essentials.api.home.Home
import ink.pmc.essentials.api.warp.Warp
import ink.pmc.menu.inspecting
import ink.pmc.utils.concurrent.submitAsync
import org.bukkit.entity.Player

class MainMenuModel(private val player: Player) : ScreenModel {
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
            val home = Essentials.homeManager.getPreferredHome(player)
            preferredHomeState = if (home != null) PreferredHomeState.Ready(home) else PreferredHomeState.None
        }
    }

    fun refreshPreferredSpawn() {
        submitAsync {
            val spawn = Essentials.warpManager.getPreferredSpawn(player)
            val defaultSpawn = Essentials.warpManager.getDefaultSpawn()
            preferredSpawnState = when {
                spawn != null -> PreferredSpawnState.Ready(spawn)
                defaultSpawn != null -> PreferredSpawnState.Ready(defaultSpawn)
                else -> PreferredSpawnState.None
            }
        }
    }

    var preferredHomeState by mutableStateOf<PreferredHomeState>(PreferredHomeState.Loading)
    var preferredSpawnState by mutableStateOf<PreferredSpawnState>(PreferredSpawnState.Loading)
    var lookupModeEnabled by mutableStateOf(player.inspecting)
}