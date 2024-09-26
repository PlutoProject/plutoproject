package ink.pmc.interactive.api.gui.form.types

import ink.pmc.interactive.api.gui.form.FormNode
import ink.pmc.interactive.api.gui.form.FormResultHandler
import ink.pmc.interactive.api.gui.form.RootFormNode
import ink.pmc.interactive.api.gui.form.components.simple.FormButtonNode
import ink.pmc.utils.bedrock.bedrockSerializer
import ink.pmc.utils.bedrock.useBedrockColors
import net.kyori.adventure.text.Component
import org.geysermc.cumulus.form.SimpleForm
import org.geysermc.cumulus.response.SimpleFormResponse
import org.geysermc.cumulus.response.result.ValidFormResponseResult
import org.geysermc.floodgate.api.player.FloodgatePlayer
import java.util.*

@Suppress("UNUSED")
internal class SimpleFormNode(
    override var title: Component,
    internal var content: Component,
    override var resultHandler: FormResultHandler<SimpleForm, SimpleFormResponse>,
) : RootFormNode<SimpleForm.Builder, SimpleForm, SimpleFormResponse> {

    override val children: LinkedList<FormNode<SimpleForm.Builder, SimpleForm>> = LinkedList()
    override val builder: SimpleForm.Builder.() -> Unit = {}

    override fun render(player: FloodgatePlayer) {
        SimpleForm.builder()
            .title(bedrockSerializer.serialize(title.useBedrockColors()))
            .content(bedrockSerializer.serialize(content.useBedrockColors()))
            .resultHandler { f, r ->
                if (r.isValid) {
                    val rsp = (r as ValidFormResponseResult).response()
                    val button = children[rsp.clickedButtonId()] as FormButtonNode
                    button.onClick(f, rsp)
                }
                resultHandler(f, r)
            }
            .apply { children.forEach { it.builder(this) } }
            .build()
            .also { player.sendForm(it) }
    }

}