package ink.pmc.interactive.api.form.components.custom

import ink.pmc.interactive.api.form.FormNode
import ink.pmc.utils.bedrock.bedrockSerializer
import ink.pmc.utils.bedrock.useBedrockColors
import net.kyori.adventure.text.Component
import org.geysermc.cumulus.form.CustomForm
import org.geysermc.floodgate.api.player.FloodgatePlayer
import java.util.*

internal class FormStepSliderNode(
    internal var text: Component,
    internal var steps: Collection<String>,
    internal var defaultStep: Int,
) : FormNode<CustomForm.Builder, CustomForm> {

    override val children: LinkedList<FormNode<CustomForm.Builder, CustomForm>> = LinkedList()
    override val builder: CustomForm.Builder.() -> Unit = {
        stepSlider(bedrockSerializer.serialize(text.useBedrockColors()), defaultStep, *steps.toTypedArray())
    }

    override fun render(player: FloodgatePlayer) {
    }

}