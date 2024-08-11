package ink.pmc.interactive.form

import androidx.compose.runtime.Applier
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionLocalProvider
import ink.pmc.interactive.UI_RENDER_FAILED
import ink.pmc.interactive.api.ComposableFunction
import ink.pmc.interactive.api.LocalGuiScope
import ink.pmc.interactive.api.LocalPlayer
import ink.pmc.interactive.api.form.GeneralFormNode
import ink.pmc.interactive.plugin
import ink.pmc.interactive.scope.BaseScope
import org.bukkit.entity.Player
import org.geysermc.floodgate.api.FloodgateApi
import java.util.logging.Level

internal val floodgateApi = FloodgateApi.getInstance()

class FormScope(
    owner: Player,
    contents: ComposableFunction
) : BaseScope<GeneralFormNode>(owner, contents) {

    override val rootNode: GeneralFormNode = FormNodeWrapper()
    override val nodeApplier: Applier<GeneralFormNode> = FormNodeApplier(rootNode) {
        val player = checkNotNull(floodgateApi.getPlayer(owner.uniqueId)) {
            dispose()
            "Forms can only render to a Floodgate player"
        }
        runCatching {
            rootNode.render(player)
            renderSignal?.complete(Unit)
            hasFrameWaiters = false
        }.onFailure {
            renderExceptionCallback(it)
        }
    }
    override val composition: Composition = Composition(nodeApplier, recomposer).apply {
        setContent {
            CompositionLocalProvider(
                LocalGuiScope provides this@FormScope,
                LocalPlayer provides owner
            ) {
                runCatching {
                    contents()
                }.onFailure {
                    renderExceptionCallback(it)
                }
            }
        }
    }

    private fun renderExceptionCallback(e: Throwable) {
        this.dispose()
        owner.sendMessage(UI_RENDER_FAILED)
        plugin.logger.log(Level.SEVERE, "Form render failed while rendering for ${owner.name}", e)
    }

}