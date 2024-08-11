package ink.pmc.interactive.form

import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.currentComposer
import ink.pmc.interactive.api.form.FormNode
import org.geysermc.cumulus.form.Form

@Suppress("UNUSED")
class FormNodeApplier<B, F : Form>(root: FormNode<B, F>, private val endChangesCallback: () -> Unit) :
    AbstractApplier<FormNode<B, F>>(root) {

    override fun onEndChanges() {
        endChangesCallback()
    }

    override fun insertBottomUp(index: Int, instance: FormNode<B, F>) {
        current.children.add(index, instance)
    }

    override fun insertTopDown(index: Int, instance: FormNode<B, F>) {
        // 只从下向上插入
    }

    override fun move(from: Int, to: Int, count: Int) {
        current.children.move(from, to, count)
    }

    override fun onClear() {
        current.children.clear()
    }

    override fun remove(index: Int, count: Int) {
        if (current.children.getOrNull(index) == null) return
        current.children.remove(index, count)
    }

}