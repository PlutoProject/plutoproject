package ink.pmc.visual

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import ink.pmc.visual.api.toast.IToastFactory
import ink.pmc.visual.api.toast.PaperToastRenderers
import ink.pmc.visual.api.toast.ToastRenderer
import ink.pmc.visual.renderers.PaperToastRenderer
import org.bukkit.event.Listener

@Suppress("UNUSED")
class PaperPlugin : SuspendingJavaPlugin(), Listener {

    override suspend fun onEnableAsync() {
        PaperToastRenderers.nmsRenderer = PaperToastRenderer
        ToastRenderer.defaultRenderer = PaperToastRenderers.nmsRenderer
        IToastFactory.instance = ToastFactoryImpl
    }
}