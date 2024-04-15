package ink.pmc.common.member.api.fetcher

import com.google.gson.JsonParser
import ink.pmc.common.utils.bedrock.uuid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.util.*

private const val globalApi = "https://api.geysermc.org/v2/"

@Suppress("UNUSED")
object BedrockProfileFetcher : AbstractProfileFetcher() {

    @OptIn(ExperimentalStdlibApi::class)
    override suspend fun fetch(name: String): UUID? {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(globalApi + "xbox/xuid/${name.lowercase()}")
                    .build()
                val call = httpClient.newCall(request)

                val response = call.execute()
                val body = response.body
                val jsonObject = JsonParser.parseString(body.string()).asJsonObject ?: return@withContext null
                val element = jsonObject.get("xuid") ?: return@withContext null
                // 显式指定包名，否则 IDEA 清理代码的 bug 会删掉 HexFormat 的 import
                val xuid = element.asLong.toHexString(kotlin.text.HexFormat.Default)
                val uuid = xuid.uuid ?: return@withContext null

                return@withContext uuid
            } catch (e: Exception) {
                null
            }
        }
    }

}