package ink.pmc.utils.multiplaform.player

import ink.pmc.advkt.component.RootComponentKt
import ink.pmc.advkt.sound.SoundKt
import ink.pmc.advkt.title.TitleKt
import ink.pmc.utils.multiplaform.SenderWrapper
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import java.util.UUID

@Suppress("UNUSED")
interface PlayerWrapper<T> : SenderWrapper<T> {

    val uuid: UUID
    val name: String
    val displayName: Component
    val isFloodgate: Boolean

    fun showTitle(content: Title)

    fun showTitle(content: TitleKt.() -> Unit)

    fun sendActionBar(content: Component)

    fun sendActionBar(content: RootComponentKt.() -> Unit)

    fun playSound(sound: Sound)

    fun playSound(sound: SoundKt.() -> Unit)

    fun hasPermission(permission: String): Boolean

    suspend fun switchServer(id: String)

}