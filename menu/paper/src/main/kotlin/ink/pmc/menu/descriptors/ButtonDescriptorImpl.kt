package ink.pmc.menu.descriptors

import ink.pmc.menu.api.descriptors.ButtonDescriptor

data class ButtonDescriptorImpl(
    override val id: String,
    override val pageId: String
) : ButtonDescriptor