package ink.pmc.interactive.api.gui.form.types

import ink.pmc.interactive.api.gui.form.FormNode
import ink.pmc.interactive.api.gui.form.FormResultHandler
import ink.pmc.interactive.api.gui.form.RootFormNode
import ink.pmc.utils.bedrock.bedrockSerializer
import ink.pmc.utils.bedrock.useBedrockColors
import net.kyori.adventure.text.Component
import org.geysermc.cumulus.form.ModalForm
import org.geysermc.cumulus.response.ModalFormResponse
import org.geysermc.cumulus.response.result.ValidFormResponseResult
import org.geysermc.floodgate.api.player.FloodgatePlayer
import java.util.*

typealias ModalResultHandler = (ModalForm, ModalFormResponse) -> Unit

@Suppress("UNUSED")
internal class ModalFormNode(
    override var title: Component,
    internal var content: Component,
    internal var button1: Component,
    internal var button2: Component,
    internal var onButton1: ModalResultHandler,
    internal var onButton2: ModalResultHandler,
    override var resultHandler: FormResultHandler<ModalForm, ModalFormResponse>,
) : RootFormNode<ModalForm.Builder, ModalForm, ModalFormResponse> {

    override val children: LinkedList<FormNode<ModalForm.Builder, ModalForm>> = LinkedList()
    override val builder: ModalForm.Builder.() -> Unit = {}

    override fun render(player: FloodgatePlayer) {
        ModalForm.builder()
            .title(bedrockSerializer.serialize(title.useBedrockColors()))
            .content(bedrockSerializer.serialize(content.useBedrockColors()))
            .button1(bedrockSerializer.serialize(button1.useBedrockColors()))
            .button2(bedrockSerializer.serialize(button2.useBedrockColors()))
            .resultHandler { f, r ->
                if (r.isValid) {
                    val rsp = (r as ValidFormResponseResult).response()
                    if (rsp.clickedFirst()) onButton1(f, rsp) else onButton2(f, rsp)
                }
                resultHandler(f, r)
            }
            .build()
            .also {
                player.sendForm(it)
            }
    }

}