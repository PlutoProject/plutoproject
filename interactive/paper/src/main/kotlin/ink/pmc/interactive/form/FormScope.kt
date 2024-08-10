package ink.pmc.interactive.form

import androidx.compose.runtime.Applier
import androidx.compose.runtime.Composition
import ink.pmc.interactive.scope.AbstractScope
import ink.pmc.interactive.api.ComposableFunction
import ink.pmc.interactive.api.form.FormNode
import ink.pmc.utils.concurrent.submitAsync
import kotlinx.coroutines.CompletableDeferred
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.Player
import org.geysermc.cumulus.form.Form
import org.geysermc.floodgate.api.FloodgateApi

internal val floodgateApi = FloodgateApi.getInstance()
internal val legacySerializer = LegacyComponentSerializer.legacySection()

private typealias GeneralForm = FormNode<Any, Form>

class FormScope(
    owner: Player,
    override val rootNode: GeneralForm,
    contents: ComposableFunction
) : AbstractScope<GeneralForm>(owner, contents) {

    override val nodeApplier: Applier<GeneralForm> = FormNodeApplier(rootNode) {
        val player = checkNotNull(floodgateApi.getPlayer(owner.uniqueId)) {
            submitAsync { dispose() }
            "Form can only be rendered to Geyser players"
        }
        rootNode.render(player)
        renderSignal?.complete(Unit)
        hasFrameWaiters = false
    }
    override val composition: Composition = Composition(nodeApplier, recomposer).apply {
        setContent {
            contents()
        }
    }

}