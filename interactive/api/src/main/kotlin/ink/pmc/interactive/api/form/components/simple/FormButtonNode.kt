package ink.pmc.interactive.api.form.components.simple

import ink.pmc.interactive.api.form.FormNode
import ink.pmc.utils.bedrock.bedrockSerializer
import ink.pmc.utils.bedrock.useBedrockColors
import net.kyori.adventure.text.Component
import org.geysermc.cumulus.form.SimpleForm
import org.geysermc.cumulus.util.FormImage
import org.geysermc.floodgate.api.player.FloodgatePlayer
import java.util.*

internal class FormButtonNode(
    internal var text: Component,
    internal var image: FormImage?,
    internal var onClick: SimpleFormButtonHandler
) : FormNode<SimpleForm.Builder, SimpleForm> {

    override val children: LinkedList<FormNode<SimpleForm.Builder, SimpleForm>> = LinkedList()
    override val builder: SimpleForm.Builder.() -> Unit = {
        if (image != null) {
            button(bedrockSerializer.serialize(text.useBedrockColors()), image)
        } else {
            button(bedrockSerializer.serialize(text.useBedrockColors()))
        }
    }

    override fun render(player: FloodgatePlayer) {
    }

}