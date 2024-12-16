package ink.pmc.menu

import ink.pmc.menu.api.ButtonDescriptor

data class ButtonDescriptorImpl(
    override val id: String,
    override val pageId: String
) : ButtonDescriptor