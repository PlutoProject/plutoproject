package ink.pmc.interactive.api.form.components.custom

import ink.pmc.interactive.api.form.FormNode
import ink.pmc.utils.bedrock.bedrockSerializer
import ink.pmc.utils.bedrock.useFallbackColors
import net.kyori.adventure.text.Component
import org.geysermc.cumulus.form.CustomForm
import org.geysermc.floodgate.api.player.FloodgatePlayer
import java.util.*

internal class FormLabelNode(
    internal var text: Component,
) : FormNode<CustomForm.Builder, CustomForm> {

    override val children: LinkedList<FormNode<CustomForm.Builder, CustomForm>> = LinkedList()
    override val builder: CustomForm.Builder.() -> Unit = {
        label(bedrockSerializer.serialize(text.useFallbackColors()))
    }

    override fun render(player: FloodgatePlayer) {
    }

}