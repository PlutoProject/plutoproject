package ink.pmc.visual

interface Renderer<P, T> {

    fun render(player: P, obj: T)

}