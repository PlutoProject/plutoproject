package ink.pmc.interactive.form

import androidx.compose.runtime.Applier
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionLocalProvider
import ink.pmc.interactive.api.ComposableFunction
import ink.pmc.interactive.api.LocalInteractiveScope
import ink.pmc.interactive.api.LocalPlayer
import ink.pmc.interactive.api.form.FormNode
import ink.pmc.interactive.scope.BaseScope
import org.bukkit.entity.Player
import org.geysermc.cumulus.form.Form
import org.geysermc.floodgate.api.FloodgateApi

internal val floodgateApi = FloodgateApi.getInstance()

private typealias GeneralForm = FormNode<Any, Form>

class FormScope(
    owner: Player,
    override val rootNode: GeneralForm,
    contents: ComposableFunction
) : BaseScope<GeneralForm>(owner, contents) {

    override val nodeApplier: Applier<GeneralForm> = FormNodeApplier(rootNode) {
        val player = checkNotNull(floodgateApi.getPlayer(owner.uniqueId)) {
            dispose()
            "Forms can only render to a Floodgate player"
        }
        rootNode.render(player)
        renderSignal?.complete(Unit)
        hasFrameWaiters = false
    }
    override val composition: Composition = Composition(nodeApplier, recomposer).apply {
        setContent {
            CompositionLocalProvider(
                LocalInteractiveScope provides this@FormScope,
                LocalPlayer provides owner
            ) {
                contents()
            }
        }
    }

}