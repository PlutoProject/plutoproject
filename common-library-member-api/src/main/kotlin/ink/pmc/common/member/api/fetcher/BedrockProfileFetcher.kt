package ink.pmc.common.member.api.fetcher

import com.google.gson.JsonParser
import ink.pmc.common.utils.bedrock.uuid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.util.UUID

private const val globalApi = "https://api.geysermc.org/v2/"

@Suppress("UNUSED")
object BedrockProfileFetcher : AbstractProfileFetcher() {

    override suspend fun fetch(name: String): ProfileData? {
        return try {
            val fetchedXuid = lookupId(name) ?: return null
            val fetchedName = lookupName(fetchedXuid) ?: return null
            ProfileData(hexedXuid(fetchedXuid), fetchedName)
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun lookupId(name: String): Long? = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${globalApi}xbox/xuid/${name.lowercase()}")
            .build()

        val call = httpClient.newCall(request)

        val response = call.execute()
        val body = response.body
        val jsonObject = JsonParser.parseString(body.string()).asJsonObject ?: return@withContext null

        jsonObject.get("xuid").asLong
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun hexedXuid(xuid: Long): UUID {
        val hexXuid = xuid.toHexString(HexFormat.Default)
        return hexXuid.uuid!!
    }

    private suspend fun lookupName(xuid: Long): String? = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${globalApi}xbox/gamertag/$xuid")
            .build()

        val call = httpClient.newCall(request)

        val response = call.execute()
        val body = response.body
        val jsonObject = JsonParser.parseString(body.string()).asJsonObject ?: return@withContext null

        jsonObject.get("gamertag").asString ?: return@withContext null
    }

}