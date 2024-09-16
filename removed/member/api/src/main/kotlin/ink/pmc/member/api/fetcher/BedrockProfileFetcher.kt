package ink.pmc.member.api.fetcher

import com.google.gson.JsonParser
import ink.pmc.utils.bedrock.uuidFromXuid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.Request
import org.jsoup.Jsoup
import java.util.UUID

private const val globalApi = "https://api.geysermc.org/v2/"

@Suppress("UNUSED")
object BedrockProfileFetcher : AbstractProfileFetcher() {

    override suspend fun fetch(name: String): ProfileData? = coroutineScope {
        val geyser = async { fetchViaGeyser(name) }
        val web = async { fetchViaWeb(name) }

        val result = select<ProfileData?> {
            geyser.onAwait.invoke { if (it != null) return@invoke it else null }
            web.onAwait.invoke { if (it != null) return@invoke it else null }
        }

        if (result == null && !geyser.isCompleted) {
            return@coroutineScope geyser.await()
        }

        if (result == null && !web.isCompleted) {
            return@coroutineScope web.await()
        }

        result
    }

    private suspend fun fetchViaGeyser(name: String): ProfileData? {
        return try {
            val fetchedXuid = lookupId(name) ?: return null
            val fetchedName = lookupName(fetchedXuid) ?: return null
            ProfileData(hexedXuid(fetchedXuid), fetchedName)
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun fetchViaWeb(name: String): ProfileData? {
        return try {
            val fetched = getInformationByGamertag(name) ?: return null
            val fetchedXuid = fetched["xuid-hex"]?.uuidFromXuid ?: return null
            val fetchedName = fetched["gamertag"] ?: return null
            ProfileData(fetchedXuid, fetchedName)
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
        return hexXuid.uuidFromXuid!!
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

    /*
    * 源代码来自 https://github.com/carlop3333/XUIDGrabber。
    * 由 GPT-4 转换成 Kotlin 并人工修缮。
    * */
    private suspend fun getInformationByGamertag(gamertag: String): Map<String, String>? = withContext(Dispatchers.IO) {
        // 从网页获取 token 和 cookie
        val initialRequest = Request.Builder()
            .url("https://www.cxkes.me/xbox/xuid")
            .build()

        val (token, cookie) = httpClient.newCall(initialRequest).execute().use {
            val html = it.body.string()
            val document = Jsoup.parse(html)
            val token = document.select("input[type=hidden]").first()?.`val`() ?: return@withContext null
            val cookies = it.headers("Set-Cookie").joinToString("; ")
            token to cookies
        }

        val formBody = FormBody.Builder()
            .add("_token", token)
            .add("gamertag", gamertag)
            .build()

        val request = Request.Builder()
            .url("https://www.cxkes.me/xbox/xuid")
            .post(formBody)
            .header("Cookie", cookie)
            .build()

        httpClient.newCall(request).execute().use {
            val html = it.body.string()
            val document = Jsoup.parse(html)

            val dataMap = mutableMapOf<String, String>()

            // 解析 gamertag
            document.select("h3.mt-2").first()?.let { element ->
                dataMap["gamertag"] = element.text()
            }

            // 解析其他信息
            document.select("div.col-md-12").first()?.let { div ->
                val xuidDec = div.select("strong:contains(XUID (DEC)) + code").text()
                val xuidHex = div.select("strong:contains(XUID (HEX)) + code").text()

                dataMap["xuid-dec"] = xuidDec
                dataMap["xuid-hex"] = xuidHex
            }

            dataMap
        }
    }

}