package ink.pmc.interactive.api.gui.form.types

import ink.pmc.interactive.api.gui.form.FormNode
import ink.pmc.interactive.api.gui.form.FormResultHandler
import ink.pmc.interactive.api.gui.form.RootFormNode
import ink.pmc.utils.bedrock.bedrockSerializer
import ink.pmc.utils.bedrock.useBedrockColors
import net.kyori.adventure.text.Component
import org.geysermc.cumulus.form.CustomForm
import org.geysermc.cumulus.response.CustomFormResponse
import org.geysermc.floodgate.api.player.FloodgatePlayer
import java.util.*

@Suppress("UNUSED")
internal class CustomFormNode(
    override var title: Component,
    override var resultHandler: FormResultHandler<CustomForm, CustomFormResponse>
) : RootFormNode<CustomForm.Builder, CustomForm, CustomFormResponse> {

    override val children: LinkedList<FormNode<CustomForm.Builder, CustomForm>> = LinkedList()
    override val builder: CustomForm.Builder.() -> Unit = {}

    override fun render(player: FloodgatePlayer) {
        CustomForm.builder()
            .title(bedrockSerializer.serialize(title.useBedrockColors()))
            .resultHandler(resultHandler)
            .apply { children.forEach { it.builder(this) } }
            .build()
            .also { player.sendForm(it) }
    }

}