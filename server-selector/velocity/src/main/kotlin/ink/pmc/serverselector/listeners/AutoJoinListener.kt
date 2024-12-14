package ink.pmc.serverselector.listeners

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent
import ink.pmc.framework.options.OptionsManager
import ink.pmc.framework.utils.platform.proxy
import ink.pmc.serverselector.AUTO_JOIN_DESCRIPTOR
import ink.pmc.serverselector.storage.UserRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.jvm.optionals.getOrNull

object AutoJoinListener : KoinComponent {
    private val userRepo by inject<UserRepository>()

    @Subscribe
    suspend fun PlayerChooseInitialServerEvent.e() {
        val uuid = player.uniqueId
        val options = OptionsManager.getOptions(uuid) ?: return
        val entry = options.getEntry(AUTO_JOIN_DESCRIPTOR) ?: return
        if (!entry.value) return
        val userModel = userRepo.find(uuid) ?: return
        val server = proxy.getServer(userModel.previouslyJoinedServer).getOrNull() ?: return
        setInitialServer(server)
    }
}