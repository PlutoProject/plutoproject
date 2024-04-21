package ink.pmc.common.utils.bedrock

import ink.pmc.common.utils.isInDebugMode
import ink.pmc.common.utils.platform.paper
import ink.pmc.common.utils.visual.*
import net.kyori.adventure.text.format.NamedTextColor
import java.lang.reflect.Method
import java.util.*

/*
* Floodgate 依赖问题，会与新版 Gson 冲突，
* 见 https://github.com/GeyserMC/Floodgate/issues/495。
* 在他们解决这个问题之前，先使用反射来判断是否为 Floodgate 玩家。
* */
lateinit var floodgateApiClass: Class<*>
lateinit var floodgateApi: Any
lateinit var isFloodgatePlayer: Method

fun floodgateSupport(): Boolean {
    return !isInDebugMode() && paper.pluginManager.getPlugin("floodgate") != null
}

fun isBedrockSession(uuid: UUID): Boolean {
    if (isInDebugMode()) {
        return false
    }

    return isFloodgatePlayer.invoke(floodgateApi, uuid) as Boolean
}

val bedrockColorMapping = mutableMapOf(
    mochaGreen to NamedTextColor.GREEN,
    mochaSapphire to NamedTextColor.AQUA,
    mochaMaroon to NamedTextColor.RED,
    mochaMauve to NamedTextColor.LIGHT_PURPLE,
    mochaYellow to NamedTextColor.YELLOW,
    mochaText to NamedTextColor.WHITE,
    mochaCrust to NamedTextColor.BLACK,
    mochaLavender to NamedTextColor.DARK_BLUE,
    mochaTeal to NamedTextColor.DARK_GREEN,
    mochaSky to NamedTextColor.DARK_AQUA,
    mochaRed to NamedTextColor.DARK_RED,
    mochaPink to NamedTextColor.DARK_PURPLE,
    mochaPeach to NamedTextColor.GOLD,
    mochaSubtext1 to NamedTextColor.GRAY,
    mochaSubtext0 to NamedTextColor.DARK_GRAY,
    mochaBlue to NamedTextColor.BLUE,
    mochaOverlay2 to materialQuartz,
    mochaOverlay1 to materialIron,
    mochaOverlay0 to materialNetherite,
    mochaRosewater to materialRedstone,
    mochaFlamingo to materialCopper,
    mochaSurface2 to materialGold,
    mochaSurface1 to materialEmerald,
    mochaSurface0 to materialDiamond,
    mochaBase to materialLapis,
    mochaMantle to materialAmethyst
)