package ink.pmc.framework.utils.player.profile

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.LoginEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object ProfileCacheListener : KoinComponent {
    private val repo by inject<ProfileCacheRepository>()

    @Subscribe
    suspend fun LoginEvent.e() {
        val cache = ProfileCache.getByUuid(player.uniqueId)
        if (cache == null) {
            repo.saveOrUpdate(CachedProfile(player.username, player.username.lowercase(), player.uniqueId))
            return
        }
        if (cache.rawName != player.username) {
            val new = cache.copy(
                rawName = player.username,
                name = player.username.lowercase()
            )
            repo.saveOrUpdate(new)
        }
    }
}