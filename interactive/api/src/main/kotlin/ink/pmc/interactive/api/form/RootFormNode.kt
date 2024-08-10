package ink.pmc.interactive.api.form

import org.geysermc.cumulus.form.Form
import org.geysermc.cumulus.response.FormResponse
import org.geysermc.cumulus.response.result.FormResponseResult

typealias FormResultHandler<F, R> = (F, FormResponseResult<R>) -> Unit

interface RootFormNode<B, F : Form, R : FormResponse> : FormNode<B, F> {

    val resultHandler: FormResultHandler<F, R>

}