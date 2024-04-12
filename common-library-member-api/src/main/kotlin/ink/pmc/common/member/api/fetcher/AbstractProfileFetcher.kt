package ink.pmc.common.member.api.fetcher

import com.google.gson.JsonParser
import ink.pmc.common.utils.player.shortUUIDToLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*

@Suppress("UNUSED")
abstract class AbstractProfileFetcher : ProfileFetcher {

    internal val httpClient = OkHttpClient()

    override suspend fun validate(name: String, uuid: UUID): Boolean {
        val fetchedId = fetch(name)
        return !(fetchedId == null || fetchedId != uuid)
    }

    override suspend fun validate(name: String, uuid: String): Boolean = validate(name, UUID.fromString(uuid))

    suspend fun yggGet(req: Request): UUID? {
        return withContext(Dispatchers.IO) {
            val call = httpClient.newCall(req)

            val response = call.execute()
            val body = response.body
            val jsonObject = JsonParser.parseString(body.string()).asJsonObject ?: return@withContext null
            val element = jsonObject.get("id") ?: return@withContext null
            val shortUUID = element.asString ?: return@withContext null

            val uuid = shortUUID.shortUUIDToLong
            UUID.fromString(uuid)
        }
    }

}