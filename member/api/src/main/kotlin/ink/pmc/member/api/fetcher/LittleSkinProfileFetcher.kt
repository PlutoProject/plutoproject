package ink.pmc.member.api.fetcher

import com.google.gson.JsonParser
import ink.pmc.utils.player.shortUUIDToLong
import ink.pmc.utils.trimmed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.commonPost
import java.util.*

private const val littleSkinApi = "https://littleskin.cn/api/yggdrasil/"

@Suppress("UNUSED")
object LittleSkinProfileFetcher : AbstractProfileFetcher() {

    override suspend fun fetch(name: String): ProfileData? {
        return try {
            val fetchedId = lookupId(name) ?: return null
            val fetchedName = lookupName(fetchedId) ?: return null
            ProfileData(fetchedId, fetchedName)
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun lookupId(name: String): UUID? = withContext(Dispatchers.IO) {
        val reqBodyJson = "[\"${name.lowercase()}\"]"
        val request = Request.Builder()
            .url("${littleSkinApi}api/profiles/minecraft/")
            .header("Content-Type", "application/json; charset=utf-8")
            .commonPost(reqBodyJson.toRequestBody("application/json".toMediaType()))
            .build()

        val call = httpClient.newCall(request)

        val response = call.execute()
        val body = response.body
        val jsonArray = JsonParser.parseString(body.string()).asJsonArray ?: return@withContext null
        val resultId = jsonArray.get(0).asJsonObject.get("id").asString.shortUUIDToLong ?: return@withContext null

        UUID.fromString(resultId)
    }

    private suspend fun lookupName(uuid: UUID): String? = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${littleSkinApi}sessionserver/session/minecraft/profile/${uuid.trimmed}")
            .build()

        val call = httpClient.newCall(request)

        val response = call.execute()
        val body = response.body
        val jsonObject = JsonParser.parseString(body.string()).asJsonObject ?: return@withContext null

        jsonObject.get("name").asString ?: return@withContext null
    }
}