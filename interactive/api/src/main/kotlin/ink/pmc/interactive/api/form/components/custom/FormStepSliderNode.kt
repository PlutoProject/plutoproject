package ink.pmc.interactive.api.form.components.custom

import ink.pmc.interactive.api.form.FormNode
import ink.pmc.interactive.api.form.legacySerializer
import ink.pmc.utils.bedrock.useFallbackColors
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
        stepSlider(legacySerializer.serialize(text.useFallbackColors()), defaultStep, *steps.toTypedArray())
    }

    override fun render(player: FloodgatePlayer) {
    }

}