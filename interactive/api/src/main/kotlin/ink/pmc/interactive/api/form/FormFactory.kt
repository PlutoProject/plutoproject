package ink.pmc.interactive.api.form

import net.kyori.adventure.text.Component
import org.geysermc.cumulus.form.ModalForm
import org.geysermc.cumulus.response.ModalFormResponse

interface FormFactory {

    fun createModalForm(
        title: Component = Component.empty(),
        content: Component = Component.empty(),
        button1: Component = Component.empty(),
        button2: Component = Component.empty(),
        resultHandler: FormResultHandler<ModalForm, ModalFormResponse> = { _, _ -> }
    ): RootFormNode<ModalForm.Builder, ModalForm, ModalFormResponse>

}