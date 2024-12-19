package ink.pmc.framework.player

import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*

private val httpClient = OkHttpClient()
private const val mojangAPI = "https://api.mojang.com/"

val String.shortUUIDToLong: String?
    get() {
        if (this.length != 32) return null
        return this.replace(
            Regex("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{12})"),
            "$1-$2-$3-$4-$5"
        )
    }

suspend fun String.fetchPlayerUuidFromApi(): UUID? {
    val str = this
    return withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(mojangAPI + "users/profiles/minecraft/$str")
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

inline val String.uuid: UUID
    get() = UUID.fromString(this)

val String.uuidOrNull: UUID?
    get() = try {
        UUID.fromString(this)
    } catch (e: IllegalArgumentException) {
        null
    }