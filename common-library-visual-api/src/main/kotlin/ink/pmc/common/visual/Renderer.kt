package ink.pmc.common.visual

interface Renderer<P, T> {

    fun render(player: P, obj: T)

}