package ink.pmc.common.member

import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*

val httpClient = OkHttpClient()
const val mojangAPI = "https://api.mojang.com/"

fun convertShortUUIDToLong(shortUUID: String): String {
    if (shortUUID.length != 32) {
        throw IllegalArgumentException("Invalid UUID string: $shortUUID")
    }

    return shortUUID.replace(
        Regex("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{12})"),
        "$1-$2-$3-$4-$5"
    )
}

suspend fun getUUIDFromMojang(name: String): UUID? {
    try {
        val request = Request.Builder()
            .url(mojangAPI + "users/profiles/minecraft/$name")
            .build()
        val call = httpClient.newCall(request)

        return withContext(Dispatchers.IO) {
            val response = call.execute()
            val body = response.body
            val jsonObject = JsonParser.parseString(body.string()).asJsonObject ?: return@withContext null
            val element = jsonObject.get("id") ?: return@withContext null
            val shortUUID = element.asString ?: return@withContext null

            val uuid = convertShortUUIDToLong(shortUUID)
            UUID.fromString(uuid)
        }
    } catch (e: Exception) {
        // 防止其他可能的问题
        return null
    }
}