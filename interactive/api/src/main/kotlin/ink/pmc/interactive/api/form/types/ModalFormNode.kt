package ink.pmc.interactive.api.form.types

import ink.pmc.interactive.api.form.FormNode
import ink.pmc.interactive.api.form.FormResultHandler
import ink.pmc.interactive.api.form.RootFormNode
import ink.pmc.interactive.api.form.legacySerializer
import ink.pmc.utils.bedrock.useFallbackColors
import net.kyori.adventure.text.Component
import org.geysermc.cumulus.form.ModalForm
import org.geysermc.cumulus.response.ModalFormResponse
import org.geysermc.floodgate.api.player.FloodgatePlayer
import java.util.*

@Suppress("UNUSED")
internal class ModalFormNode(
    override var title: Component,
    internal var content: Component,
    internal var button1: Component,
    internal var button2: Component,
    override var resultHandler: FormResultHandler<ModalForm, ModalFormResponse>,
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