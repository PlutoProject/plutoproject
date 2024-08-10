package ink.pmc.interactive.api.form.types

import ink.pmc.interactive.api.form.FormNode
import ink.pmc.interactive.api.form.FormResultHandler
import ink.pmc.interactive.api.form.RootFormNode
import ink.pmc.interactive.api.form.legacySerializer
import ink.pmc.utils.bedrock.useFallbackColors
import net.kyori.adventure.text.Component
import org.geysermc.cumulus.form.SimpleForm
import org.geysermc.cumulus.response.SimpleFormResponse
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
            .title(legacySerializer.serialize(title.useFallbackColors()))
            .content(legacySerializer.serialize(content.useFallbackColors()))
            .apply { children.forEach { it.builder(this) } }
            .build()
            .also { player.sendForm(it) }
    }

}