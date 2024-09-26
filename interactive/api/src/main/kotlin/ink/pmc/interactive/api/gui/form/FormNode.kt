package ink.pmc.interactive.api.gui.form

import org.geysermc.cumulus.form.Form
import org.geysermc.floodgate.api.player.FloodgatePlayer
import java.util.*

typealias GeneralFormNode = FormNode<Any, Form>

interface FormNode<B, F : Form> {

    val children: LinkedList<FormNode<B, F>>
    val builder: B.() -> Unit

    fun render(player: FloodgatePlayer)

}