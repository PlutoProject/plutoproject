package ink.pmc.common.member.api.fetcher

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

    override suspend fun fetch(name: String): UUID? {
        return withContext(Dispatchers.IO) {
            try {
                val reqBodyJson = "[\"${name.lowercase()}\"]"
                val request = Request.Builder()
                    .url(littleSkinApi + "api/profiles/minecraft/")
                    .header("Content-Type", "application/json; charset=utf-8")
                    .commonPost(reqBodyJson.toRequestBody("application/json".toMediaType()))
                    .build()

                return@withContext yggGet(request)
            } catch (e: Exception) {
                null
            }
        }
    }

}