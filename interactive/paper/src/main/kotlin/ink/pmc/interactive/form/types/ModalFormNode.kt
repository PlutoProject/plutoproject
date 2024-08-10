package ink.pmc.interactive.form.types

import ink.pmc.interactive.api.form.FormNode
import ink.pmc.interactive.api.form.FormResultHandler
import ink.pmc.interactive.api.form.RootFormNode
import ink.pmc.interactive.form.legacySerializer
import ink.pmc.utils.bedrock.useFallbackColors
import net.kyori.adventure.text.Component
import org.geysermc.cumulus.form.ModalForm
import org.geysermc.cumulus.response.ModalFormResponse
import org.geysermc.floodgate.api.player.FloodgatePlayer
import java.util.*

@Suppress("UNUSED")
class ModalFormNode(
    override val title: Component,
    private val content: Component,
    private val button1: Component,
    private val button2: Component,
    override val resultHandler: FormResultHandler<ModalForm, ModalFormResponse>,
) : RootFormNode<ModalForm.Builder, ModalForm, ModalFormResponse> {

    override val children: LinkedList<FormNode<ModalForm.Builder, ModalForm>> = LinkedList()
    override val builder: ModalForm.Builder.() -> Unit = {}

    override fun render(player: FloodgatePlayer) {
        ModalForm.builder()
            .title(legacySerializer.serialize(title.useFallbackColors()))
            .content(legacySerializer.serialize(content.useFallbackColors()))
            .button1(legacySerializer.serialize(button1.useFallbackColors()))
            .button2(legacySerializer.serialize(button2.useFallbackColors()))
            .resultHandler(resultHandler)
            .build()
            .also { player.sendForm(it) }
    }

}