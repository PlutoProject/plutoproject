package ink.pmc.common.page

import ink.pmc.common.component.ContainerComponent

@Suppress("UNUSED")
interface Page {

    val name: String
    val components: List<ContainerComponent>

}