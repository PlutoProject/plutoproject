package ink.pmc.interactive.api.form

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.geysermc.cumulus.form.Form
import org.geysermc.floodgate.api.player.FloodgatePlayer
import java.util.*

internal val legacySerializer = LegacyComponentSerializer.legacySection()

typealias GeneralFormNode = FormNode<Any, Form>

interface FormNode<B, F : Form> {

    val children: LinkedList<FormNode<B, F>>
    val builder: B.() -> Unit

    fun render(player: FloodgatePlayer)

}