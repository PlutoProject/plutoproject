package ink.pmc.framework.player.profile

import com.google.gson.JsonParser
import ink.pmc.framework.player.shortUUIDToLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.util.*

private const val mojangApi = "https://api.mojang.com/"

@Suppress("UNUSED")
object MojangProfileFetcher : AbstractProfileFetcher() {
    override val id: String = "mojang"

    override suspend fun fetch(name: String): ProfileData? = try {
        lookup(name)
    } catch (e: Exception) {
        null
    }

    private suspend fun lookup(name: String): ProfileData? = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${mojangApi}users/profiles/minecraft/${name.lowercase()}")
            .build()

        val call = httpClient.newCall(request)

        val response = call.execute()
        val body = response.body
        val jsonObject = JsonParser.parseString(body.string()).asJsonObject ?: return@withContext null

        val resultId = jsonObject.get("id").asString.shortUUIDToLong ?: return@withContext null
        val resultName = jsonObject.get("name").asString ?: return@withContext null

        val uuid = UUID.fromString(resultId)

        ProfileData(uuid, resultName)
    }
}