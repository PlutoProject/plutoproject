package ink.pmc.interactive.api.form.types

import ink.pmc.interactive.api.form.FormNode
import ink.pmc.interactive.api.form.FormResultHandler
import ink.pmc.interactive.api.form.RootFormNode
import ink.pmc.interactive.api.form.legacySerializer
import ink.pmc.utils.bedrock.useFallbackColors
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
            .title(legacySerializer.serialize(title.useFallbackColors()))
            .apply { children.forEach { it.builder(this) } }
            .build()
            .also { player.sendForm(it) }
    }

}