package ink.pmc.interactive.api.gui.form

import net.kyori.adventure.text.Component
import org.geysermc.cumulus.form.Form
import org.geysermc.cumulus.response.FormResponse
import org.geysermc.cumulus.response.result.FormResponseResult

typealias FormResultHandler<F, R> = (F, FormResponseResult<R>) -> Unit

interface RootFormNode<B, F : Form, R : FormResponse> : FormNode<B, F> {

    var title: Component
    var resultHandler: FormResultHandler<F, R>

}