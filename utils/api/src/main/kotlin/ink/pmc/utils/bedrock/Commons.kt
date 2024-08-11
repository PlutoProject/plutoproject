package ink.pmc.utils.bedrock

import ink.pmc.utils.chat.replaceColor
import ink.pmc.utils.visual.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.serializer.legacy.CharacterAndFormat
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.geysermc.floodgate.api.FloodgateApi
import java.util.*

val floodgateApi: FloodgateApi?
    get() {
        // if (!velocityHasFloodgateSupport()) return null
        return FloodgateApi.getInstance()
    }

fun isFloodgatePlayer(uuid: UUID): Boolean {
    return floodgateApi?.getPlayer(uuid) != null
}

val TextColor.bedrock: TextColor
    get() = bedrockColorMappings[this] ?: this

val bedrockColorMappings = mapOf(
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

val bedrockFormats = listOf(
    CharacterAndFormat.characterAndFormat('h', mochaOverlay2),
    CharacterAndFormat.characterAndFormat('i', mochaOverlay1),
    CharacterAndFormat.characterAndFormat('j', mochaOverlay0),
    CharacterAndFormat.characterAndFormat('m', mochaRosewater),
    CharacterAndFormat.characterAndFormat('n', mochaFlamingo),
    CharacterAndFormat.characterAndFormat('p', mochaSurface2),
    CharacterAndFormat.characterAndFormat('q', mochaSurface1),
    CharacterAndFormat.characterAndFormat('s', mochaSurface0),
    CharacterAndFormat.characterAndFormat('t', mochaBase),
    CharacterAndFormat.characterAndFormat('u', mochaMantle)
)

val bedrockSerializer = LegacyComponentSerializer.legacySection()

fun Component.useBedrockColors(): Component {
    return bedrockColorMappings.entries.fold(this) { currentComponent, it ->
        currentComponent.replaceColor(it.key, it.value)
    }
}