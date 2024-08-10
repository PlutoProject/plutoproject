package ink.pmc.interactive.api.form

import net.kyori.adventure.text.Component
import org.geysermc.cumulus.form.CustomForm
import org.geysermc.cumulus.form.ModalForm
import org.geysermc.cumulus.form.SimpleForm
import org.geysermc.cumulus.response.CustomFormResponse
import org.geysermc.cumulus.response.ModalFormResponse
import org.geysermc.cumulus.response.SimpleFormResponse

interface FormFactory {

    fun createModalForm(
        title: Component = Component.empty(),
        content: Component = Component.empty(),
        button1: Component = Component.empty(),
        button2: Component = Component.empty(),
        resultHandler: FormResultHandler<ModalForm, ModalFormResponse> = { _, _ -> }
    ): RootFormNode<ModalForm.Builder, ModalForm, ModalFormResponse>

    fun createSimpleForm(
        title: Component = Component.empty(),
        content: Component = Component.empty(),
        resultHandler: FormResultHandler<SimpleForm, SimpleFormResponse> = { _, _ -> }
    ): RootFormNode<SimpleForm.Builder, SimpleForm, SimpleFormResponse>

    fun createCustomForm(
        title: Component = Component.empty(),
        resultHandler: FormResultHandler<CustomForm, CustomFormResponse> = { _, _ -> }
    ): RootFormNode<CustomForm.Builder, CustomForm, CustomFormResponse>

}