package ink.pmc.framework.utils.player.profile

import ink.pmc.framework.player.profile.CachedProfile
import ink.pmc.framework.player.profile.ProfileCache
import ink.pmc.framework.player.profile.MojangProfileFetcher
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class ProfileCacheImpl : ProfileCache, KoinComponent {
    private val repo by inject<ProfileCacheRepository>()

    override suspend fun getByName(name: String): CachedProfile? {
        return repo.findByName(name) ?: MojangProfileFetcher.fetch(name)?.let {
            CachedProfile(it.name, it.name.lowercase(), it.uuid).also { profile ->
                repo.saveOrUpdate(profile)
            }
        }
    }

    override suspend fun getByUuid(uuid: UUID): CachedProfile? {
        return repo.findByUuid(uuid)
    }
}