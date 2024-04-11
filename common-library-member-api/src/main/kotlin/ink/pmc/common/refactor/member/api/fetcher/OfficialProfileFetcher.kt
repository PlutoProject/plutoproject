package ink.pmc.common.refactor.member.api.fetcher

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.util.*

private const val mojangApi = "https://api.mojang.com/"

@Suppress("UNUSED")
object OfficialProfileFetcher : AbstractProfileFetcher() {

    override suspend fun fetch(name: String): UUID? {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(mojangApi + "users/profiles/minecraft/${name.lowercase()}")
                    .build()

                return@withContext yggGet(request)
            } catch (e: Exception) {
                null
            }
        }
    }

}