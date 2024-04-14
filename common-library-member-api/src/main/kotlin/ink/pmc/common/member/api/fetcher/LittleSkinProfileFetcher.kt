package ink.pmc.common.member.api.fetcher

import com.google.gson.JsonParser
import ink.pmc.common.utils.player.shortUUIDToLong
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

    override suspend fun fetch(name: String): UUID? = withContext(Dispatchers.IO) {
        val reqBodyJson = "[\"${name.lowercase()}\"]"
        val request = Request.Builder()
            .url(littleSkinApi + "api/profiles/minecraft/")
            .header("Content-Type", "application/json; charset=utf-8")
            .commonPost(reqBodyJson.toRequestBody("application/json".toMediaType()))
            .build()

        val call = httpClient.newCall(request)

        val response = call.execute()
        val body = response.body
        val jsonArray = JsonParser.parseString(body.string()).asJsonArray ?: return@withContext null
        val element = jsonArray.get(0).asJsonObject.get("id") ?: return@withContext null
        val shortUUID = element.asString ?: return@withContext null

        val uuid = shortUUID.shortUUIDToLong
        UUID.fromString(uuid)
    }

}