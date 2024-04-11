package ink.pmc.common.refactor.member.api.fetcher

import com.google.gson.JsonParser
import ink.pmc.common.utils.player.shortUUIDToLong
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
                val call = httpClient.newCall(request)

                val response = call.execute()
                val body = response.body
                val jsonObject = JsonParser.parseString(body.string()).asJsonObject ?: return@withContext null
                val element = jsonObject.get("id") ?: return@withContext null
                val shortUUID = element.asString ?: return@withContext null

                val uuid = shortUUID.shortUUIDToLong
                UUID.fromString(uuid)
            } catch (e: Exception) {
                // 防止其他可能的问题
                null
            }
        }
    }

}