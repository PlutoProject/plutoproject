package ink.pmc.common.utils.bedrock

import java.util.*

/*
* Floodgate 依赖问题，会与新版 Gson 冲突，
* 见 https://github.com/GeyserMC/Floodgate/issues/495。
* 在他们解决这个问题之前，先使用反射来判断是否为 Floodgate 玩家。
* */
private val floodgateApiClass = Class.forName("org.geysermc.floodgate.api.FloodgateApi")
private val floodgateApi = run {
    val method = floodgateApiClass.getDeclaredMethod("getInstance")
    method.invoke(null)
}
private val isFloodgatePlayer = floodgateApiClass.getDeclaredMethod("isFloodgatePlayer", UUID::class.java)

fun isBedrockSession(uuid: UUID): Boolean {
    return isFloodgatePlayer.invoke(floodgateApi, uuid) as Boolean
}