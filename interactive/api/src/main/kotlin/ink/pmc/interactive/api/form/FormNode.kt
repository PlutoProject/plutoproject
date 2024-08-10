package ink.pmc.interactive.api.form

import net.kyori.adventure.text.Component
import org.geysermc.cumulus.form.Form
import org.geysermc.floodgate.api.player.FloodgatePlayer
import java.util.*

interface FormNode<B, F : Form> {

    val title: Component
    val children: LinkedList<FormNode<B, F>>
    val builder: B.() -> Unit

    fun render(player: FloodgatePlayer)

}