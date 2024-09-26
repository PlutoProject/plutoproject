package ink.pmc.interactive.api.gui.form.components.custom

import ink.pmc.interactive.api.gui.form.FormNode
import ink.pmc.utils.bedrock.bedrockSerializer
import ink.pmc.utils.bedrock.useBedrockColors
import net.kyori.adventure.text.Component
import org.geysermc.cumulus.form.CustomForm
import org.geysermc.floodgate.api.player.FloodgatePlayer
import java.util.*

internal class FormDropdownNode(
    internal var text: Component,
    internal var options: Collection<String>,
    internal var defaultOption: Int
) : FormNode<CustomForm.Builder, CustomForm> {

    override val children: LinkedList<FormNode<CustomForm.Builder, CustomForm>> = LinkedList()
    override val builder: CustomForm.Builder.() -> Unit = {
        dropdown(bedrockSerializer.serialize(text.useBedrockColors()), defaultOption, *options.toTypedArray())
    }

    override fun render(player: FloodgatePlayer) {
    }

}