package ink.pmc.utils.multiplaform.player.velocity

import com.velocitypowered.api.proxy.Player
import ink.pmc.advkt.component.RootComponentKt
import ink.pmc.advkt.sound.SoundKt
import ink.pmc.advkt.title.ComponentTitleKt
import ink.pmc.advkt.title.TitleKt
import ink.pmc.utils.bedrock.isBedrock
import ink.pmc.utils.multiplaform.player.PlayerWrapper
import ink.pmc.utils.player.switchServer
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import java.util.*

class VelocityPlayerWrapper(private val player: Player) : PlayerWrapper<Player>, VelocitySenderWrapper<Player>(player) {

    override val uuid: UUID
        get() = player.uniqueId
    override val name: String
        get() = player.username
    override val displayName: Component
        get() = Component.text(name)
    override val isBedrock: Boolean
        get() = player.isBedrock

    override fun showTitle(content: Title) {
        player.showTitle(content)
    }

    override fun showTitle(content: TitleKt.() -> Unit) {
        showTitle(ComponentTitleKt().apply(content).build())
    }

    override fun sendActionBar(content: Component) {
        player.sendActionBar(content)
    }

    override fun sendActionBar(content: RootComponentKt.() -> Unit) {
        sendActionBar(RootComponentKt().apply(content).build())
    }

    override fun playSound(sound: Sound) {
        player.playSound(sound)
    }

    override fun playSound(sound: SoundKt.() -> Unit) {
        playSound(SoundKt().apply(sound).build())
    }

    override fun hasPermission(permission: String): Boolean {
        return player.hasPermission(permission)
    }

    override suspend fun switchServer(id: String) {
        player.switchServer(id)
    }

}