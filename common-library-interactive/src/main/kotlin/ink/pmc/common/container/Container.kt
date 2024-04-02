package ink.pmc.common.container

import ink.pmc.common.InteractiveAPI
import ink.pmc.common.renderer.PageRenderer
import ink.pmc.common.page.Page
import org.bukkit.entity.Player

@Suppress("UNUSED")
interface Container {

    val name: String
    val pages: Map<String, Page>
    val defaultPage
        get() = pages["_default"]

    fun open(
        player: Player,
        page: String = "_default",
        renderer: PageRenderer = InteractiveAPI.instance.containerManager.getDefaultRenderer(pages[page]!!::class.java)
    )

}